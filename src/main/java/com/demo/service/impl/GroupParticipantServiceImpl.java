package com.demo.service.impl;

import com.demo.business.GroupParticipantBO;
import com.demo.model.GroupParticipant;
import com.demo.repository.GroupParticipantDao;
import com.demo.service.GroupParticipantService;
import com.demo.utils.CommonUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupParticipantServiceImpl implements GroupParticipantService {
    private final GroupParticipantDao groupParticipantDao;

    @Autowired
    public GroupParticipantServiceImpl(GroupParticipantDao groupParticipantDao) {
        this.groupParticipantDao = groupParticipantDao;
    }
    @Override
    public GroupParticipantBO createGroupParticipant(GroupParticipantBO groupParticipantBO) {
        GroupParticipant groupParticipant = new GroupParticipant(groupParticipantBO);
        groupParticipant.setJoinedAt(CommonUtils.getCurrentISTDateTimeInString());
        GroupParticipant createdGroupParticipant = groupParticipantDao.createGroupParticipant(groupParticipant);
        return new GroupParticipantBO(createdGroupParticipant);
    }

    @Override
    public List<GroupParticipantBO> loadGroupParticipantByGroupId(String groupId) {
        List<GroupParticipant> groupParticipants = groupParticipantDao.loadGroupParticipantByGroupId(groupId);
        List<GroupParticipantBO> groupParticipantBOS = new ArrayList<>();
        for (GroupParticipant groupParticipant : groupParticipants) {
            groupParticipantBOS.add(new GroupParticipantBO(groupParticipant));
        }
        return groupParticipantBOS;
    }

    @Override
    public void deleteGroupParticipant(String groupId, String username) {
        groupParticipantDao.deleteGroupParticipant(groupId, username);
    }

    @Override
    public void deleteAllGroupParticipant(String groupId) {
        groupParticipantDao.deleteAllGroupParticipant(groupId);
    }
}
