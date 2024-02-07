package com.demo.service.impl;

import com.amazonaws.util.CollectionUtils;
import com.demo.business.UserDetailsBO;
import com.demo.business.UserJoinedGroupBO;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.UserInfoDetails;
import com.demo.model.UserJoinedGroup;
import com.demo.repository.UserDetailsDao;
import com.demo.service.GroupParticipantService;
import com.demo.service.JwtService;
import com.demo.service.UserJoinedGroupService;
import com.demo.service.UserService;
import com.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserDetailsDao userDetailsDao;
    private final JwtService jwtService;
    private final UserJoinedGroupService userJoinedGroupService;
    private final GroupParticipantService groupParticipantService;

    @Autowired
    public UserServiceImpl(UserDetailsDao userDetailsDao, JwtService jwtService,
                           UserJoinedGroupService userJoinedGroupService, GroupParticipantService groupParticipantService) {
        this.userDetailsDao = userDetailsDao;
        this.jwtService = jwtService;
        this.userJoinedGroupService = userJoinedGroupService;
        this.groupParticipantService = groupParticipantService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfoDetails userinfoDetails = fetchUserByUsername(username);
        return new User(userinfoDetails.getUsername(), userinfoDetails.getPasswordHash(),
                new ArrayList<>());
    }

    public UserDetailsBO createUser(UserDetailsBO user) {
        try {
            UserInfoDetails userInfoDetails = new UserInfoDetails(user);
            String time = CommonUtils.getCurrentISTDateTimeInString();
            userInfoDetails.setCreatedAt(time);
            userInfoDetails.setUpdatedAt(time);
            userInfoDetails.setPasswordHash(jwtService.encodePassword(user.getPassword()));
            UserInfoDetails existingUser = userDetailsDao.loadUserByUsername(user.getUsername());
            if (existingUser != null) {
                LOG.error("User already exists with username: " + user.getUsername());
                throw CommonExceptionType.USER_EXISTS.getException("User exists with same username.");
            }
            UserInfoDetails updatedUserDetails = userDetailsDao.createUser(userInfoDetails);
            return new UserDetailsBO(updatedUserDetails);
        } catch (CustomException ce) {
            LOG.error("Failed to create user: " + user.getUsername() + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to create user with username: " + user.getUsername() + " with Exception message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to create user.");
        }
    }

    public UserDetailsBO findUserByUsername(String username) {
        try {
            UserInfoDetails userInfoDetails = fetchUserByUsername(username);
            List<UserJoinedGroupBO> userJoinedGroups = userJoinedGroupService.loadUserJoinedGroupByUsername(username);
            UserDetailsBO userDetailsBO = new UserDetailsBO(userInfoDetails);
            userDetailsBO.setUserJoinedGroups(userJoinedGroups);
            return userDetailsBO;
        } catch (CustomException ce) {
            LOG.error("Failed to fetch user: " + username + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to fetch user with username: " + username + " with Exception message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException();
        }
    }

    public UserInfoDetails fetchUserByUsername(String userId) {
        UserInfoDetails userInfoDetails = userDetailsDao.loadUserByUsername(userId);
        if (userInfoDetails != null) {
            return userInfoDetails;
        }
        throw CommonExceptionType.NOT_FOUND.getException("User not found with username");
    }

    public UserDetailsBO updateUser(UserDetailsBO user) {
        try {
            UserInfoDetails newUserInfoDetails = new UserInfoDetails(user);
            UserInfoDetails currentUserInfoDetails = userDetailsDao.loadUserByUsername(user.getUsername());
            UserInfoDetails modifiedUser = updateLastRecordWithUpdatedValue(currentUserInfoDetails, newUserInfoDetails);
            UserInfoDetails updatedUser = userDetailsDao.updateUser(modifiedUser);
            return new UserDetailsBO(updatedUser);
        } catch (CustomException ce) {
            LOG.error("Failed to update user: " + user.getUsername() + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to update user with username: " + user.getUsername() + " with Exception message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException();
        }
    }

    public UserInfoDetails updateLastRecordWithUpdatedValue(UserInfoDetails currentUserDetails, UserInfoDetails newUserInfoDetails) {
        if (StringUtils.isNotBlank(newUserInfoDetails.getName())) {
            currentUserDetails.setName(newUserInfoDetails.getName());
        }
        if (StringUtils.isNotBlank(newUserInfoDetails.getEmail())) {
            currentUserDetails.setEmail(newUserInfoDetails.getEmail());
        }
        if (StringUtils.isNotBlank(newUserInfoDetails.getEmail())) {
            currentUserDetails.setEmail(newUserInfoDetails.getEmail());
        }
        if (StringUtils.isNotBlank(newUserInfoDetails.getPasswordHash())) {
            currentUserDetails.setPasswordHash(jwtService.encodePassword(newUserInfoDetails.getPasswordHash()));
        }
        return currentUserDetails;
    }


    public void deleteUser(String userId) {
        try {
            LOG.info("Got request to delete user with username: " + userId);
            UserDetailsBO userDetailsBO = findUserByUsername(userId);
            if (!CollectionUtils.isNullOrEmpty(userDetailsBO.getUserJoinedGroups())) {
                for (UserJoinedGroupBO userJoinedGroup : userDetailsBO.getUserJoinedGroups()) {
                    groupParticipantService.deleteGroupParticipant(userJoinedGroup.getGroupId(), userJoinedGroup.getUsername());
                }
                userJoinedGroupService.deleteAllUserJoinedGroup(userDetailsBO.getUsername());
            }
            userDetailsDao.deleteUser(userId);
        } catch (CustomException ce) {
            LOG.error("Failed to delete user: " + userId + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to delete user with username: " + userId + " with Exception message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException();
        }
    }
}
