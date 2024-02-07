package com.demo.service.impl;

import com.amazonaws.util.CollectionUtils;
import com.demo.business.GroupDetailsBO;
import com.demo.business.GroupParticipantBO;
import com.demo.business.UserDetailsBO;
import com.demo.business.UserJoinedGroupBO;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.*;
import com.demo.repository.GroupDetailsDao;
import com.demo.service.GroupParticipantService;
import com.demo.service.GroupService;
import com.demo.service.UserJoinedGroupService;
import com.demo.service.UserService;
import com.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {
    private final GroupDetailsDao groupDetailsDao;
    private final UserService userService;
    private final UserJoinedGroupService userJoinedGroupService;
    private final GroupParticipantService groupParticipantService;

    @Autowired
    public GroupServiceImpl(GroupDetailsDao groupDetailsDao, UserService userService,
                            UserJoinedGroupService userJoinedGroupService, GroupParticipantService groupParticipantService) {
        this.groupDetailsDao = groupDetailsDao;
        this.userService = userService;
        this.userJoinedGroupService = userJoinedGroupService;
        this.groupParticipantService = groupParticipantService;
    }

    @Override
    public GroupDetails createGroup(GroupDetails groupDetails) {
        try {
            String time = CommonUtils.getCurrentISTDateTimeInString();
            groupDetails.setCreatedAt(time);
            groupDetails.setUpdatedAt(time);
            UserDetailsBO userDetailsBO = userService.findUserByUsername(groupDetails.getCreatedBy());
            Optional<UserJoinedGroupBO> joinedGroup = userDetailsBO.getUserJoinedGroups().stream()
                    .filter(userJoinedGroup -> groupDetails.getName().equals(userJoinedGroup.getGroupDisplayName()))
                    .findFirst();
            if (joinedGroup.isPresent()) {
                LOG.info("Group already present with name: " + groupDetails.getName() + " and group id: " + joinedGroup.get().getGroupId());
                throw CommonExceptionType.GROUP_EXISTS.getException("Group exists with name");
            }
            GroupDetails createdGroup = groupDetailsDao.createGroup(groupDetails);
            GroupParticipantBO groupParticipantBO = GroupParticipantBO.builder()
                    .groupId(createdGroup.getId())
                    .username(userDetailsBO.getUsername())
                    .displayName(userDetailsBO.getName())
                    .userType(UserType.ADMIN)
                    .build();
            groupParticipantService.createGroupParticipant(groupParticipantBO);
            UserJoinedGroupBO userJoinedGroupBO = UserJoinedGroupBO.builder()
                    .groupId(createdGroup.getId())
                    .groupDisplayName(createdGroup.getName())
                    .username(userDetailsBO.getUsername())
                    .joinedAt(time)
                    .build();
            userJoinedGroupService.createUserJoinedGroup(userJoinedGroupBO);
            return createdGroup;
        } catch (CustomException ce) {
            LOG.error("Error in creating group: " + groupDetails + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Error in creating group: " + groupDetails + " with exception message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to create group.");
        }
    }

    @Override
    public GroupDetails updateGroup(GroupDetails groupDetails) {
        try {
            return groupDetailsDao.updateGroup(groupDetails);
        } catch (CustomException ce) {
            LOG.error("Error in updating group: " + groupDetails + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Error in updating group: " + groupDetails + " with exception message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to fetch group details.");
        }
    }

    @Override
    public GroupDetailsBO loadGroupDetails(String groupId) {
        try {
            GroupDetails groupDetails = groupDetailsDao.loadGroupByGroupId(groupId);
            if (groupDetails == null) {
                throw CommonExceptionType.NOT_FOUND.getException("Group: " + groupId + " not found");
            }
            List<GroupParticipantBO> groupParticipantBOS = groupParticipantService.loadGroupParticipantByGroupId(groupId);
            GroupDetailsBO groupDetailsBO = new GroupDetailsBO(groupDetails);
            groupDetailsBO.setMembers(groupParticipantBOS);
            return groupDetailsBO;
        } catch (CustomException ce) {
            LOG.error("Error in fetching group: " + groupId + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Error in fetching group: " + groupId + " with exception message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException();
        }
    }

    public void deleteGroup(String groupId) {
        try {
            GroupDetailsBO groupDetailsBO = loadGroupDetails(groupId);
            if (!CollectionUtils.isNullOrEmpty(groupDetailsBO.getMembers())) {
                for (GroupParticipantBO groupParticipant : groupDetailsBO.getMembers()) {
                    userJoinedGroupService.deleteUserJoinedGroup(groupParticipant.getUsername(), groupId);
                }
                groupParticipantService.deleteAllGroupParticipant(groupId);
            }
            groupDetailsDao.deleteGroup(groupId);
        } catch (CustomException ce) {
            LOG.error("Error in deleting group: " + groupId + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Error in deleting group: " + groupId + " with exception message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException();
        }
    }

    @Override
    public boolean addMemberToGroup(AddMemberRequest addMemberRequest) {
        try {
            LOG.info("Got request to add user: " + addMemberRequest.getUsername() + " in group: "
                    + addMemberRequest.getGroupId() + " by user: " + addMemberRequest.getAddedBy());
            GroupDetailsBO groupDetailsBO = loadGroupDetails(addMemberRequest.getGroupId());
            if (addMemberRequest.getType() == null) {
                addMemberRequest.setType(UserType.DEFAULT);
            }
            isOperationAllowed(groupDetailsBO, addMemberRequest.getAddedBy());
            UserDetailsBO userDetails = userService.findUserByUsername(addMemberRequest.getUsername());
            Optional<GroupParticipantBO> participant = CommonUtils.getGroupParticipantWithUserName(groupDetailsBO.getMembers(), addMemberRequest.getUsername());
            if (participant.isPresent()) {
                LOG.info("User: " + addMemberRequest.getUsername() + " already present in this group");
                throw CommonExceptionType.USER_OPERATION_NOT_ALLOWED.getException("User already a member of group");
            }
            updateGroupParticipant(addMemberRequest, userDetails.getName());
            updateUserJoinedGroup(groupDetailsBO.getId(), userDetails.getUsername(), groupDetailsBO.getName());
            return true;
        } catch (CustomException ce) {
            LOG.error("Error in adding member to group: " + addMemberRequest + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Error in adding member to group: " + addMemberRequest + " with exception message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to add member to group");
        }
    }

    public void updateGroupParticipant(AddMemberRequest addMemberRequest, String name) {
        GroupParticipantBO groupParticipantBO = GroupParticipantBO.builder()
                .groupId(addMemberRequest.getGroupId())
                .username(addMemberRequest.getUsername())
                .displayName(name)
                .userType(addMemberRequest.getType())
                .build();
        groupParticipantService.createGroupParticipant(groupParticipantBO);
    }

    public void updateUserJoinedGroup(String groupId, String username, String groupName) {
        UserJoinedGroupBO userJoinedGroupBO = UserJoinedGroupBO.builder()
                .groupId(groupId)
                .username(username)
                .groupDisplayName(groupName)
                .build();
        userJoinedGroupService.createUserJoinedGroup(userJoinedGroupBO);
    }


    @Override
    public boolean removeMemberFromGroup(RemoveMemberRequest removeMemberRequest) {
        try {
            LOG.info("Got request to remove user: " + removeMemberRequest.getUsername() + " from group: "
                    + removeMemberRequest.getGroupId() + " by user: " + removeMemberRequest.getRemovedBy());
            GroupDetailsBO groupDetailsBO = loadGroupDetails(removeMemberRequest.getGroupId());
            isOperationAllowed(groupDetailsBO, removeMemberRequest.getRemovedBy());
            UserDetailsBO userDetails = userService.findUserByUsername(removeMemberRequest.getUsername());
            removeParticipant(groupDetailsBO, removeMemberRequest.getUsername());
            userJoinedGroupService.deleteUserJoinedGroup(userDetails.getUsername(), removeMemberRequest.getGroupId());
            LOG.info("User: " + removeMemberRequest.getUsername() + " removed from group: " + removeMemberRequest.getGroupId());
            return true;
        } catch (CustomException ce) {
            LOG.error("Error in removing member from group: " + removeMemberRequest + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Error in removing member from group: " + removeMemberRequest + " with exception message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to remove member from group");
        }
    }

    public boolean isOperationAllowed(GroupDetailsBO groupDetailsBO, String user) {
        Optional<GroupParticipantBO> removedByMember = CommonUtils.getGroupParticipantWithUserName(groupDetailsBO.getMembers(), user);
        if (removedByMember.isEmpty() || UserType.ADMIN != removedByMember.get().getUserType()) {
            LOG.error("user: " + user + " is not admin or not present in group: " + groupDetailsBO.getId() + " add/ remove member not allowed");
            throw CommonExceptionType.USER_OPERATION_NOT_ALLOWED.getException("user is not a member of group or not admin.");
        }
        return true;
    }

    public boolean removeMemberFromGroupWhenUserDeleted(String groupId, String username) {
        GroupDetailsBO groupDetailsBO = loadGroupDetails(groupId);
        removeParticipant(groupDetailsBO, username);
        return true;
    }

    public void removeParticipant(GroupDetailsBO groupDetailsBO, String username) {
        Optional<GroupParticipantBO> participant = CommonUtils.getGroupParticipantWithUserName(groupDetailsBO.getMembers(), username);
        if (participant.isEmpty()) {
            LOG.error("user: " + username + " not a member of this group: " + groupDetailsBO.getName());
            throw CommonExceptionType.USER_OPERATION_NOT_ALLOWED.getException("user: " + username + " not a member of this group: " + groupDetailsBO.getName());
        }
        groupParticipantService.deleteGroupParticipant(groupDetailsBO.getId(), username);
    }
}
