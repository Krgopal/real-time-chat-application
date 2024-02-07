package com.demo.service.impl;

import com.demo.business.UserJoinedGroupBO;
import com.demo.model.UserJoinedGroup;
import com.demo.repository.UserJoinedGroupDao;
import com.demo.service.UserJoinedGroupService;
import com.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserJoinedGroupServiceImpl implements UserJoinedGroupService {
    private final UserJoinedGroupDao userJoinedGroupDao;

    @Autowired
    public UserJoinedGroupServiceImpl(UserJoinedGroupDao userJoinedGroupDao) {
        this.userJoinedGroupDao = userJoinedGroupDao;
    }
    @Override
    public UserJoinedGroupBO createUserJoinedGroup(UserJoinedGroupBO userJoinedGroupBO) {
        UserJoinedGroup userJoinedGroup = new UserJoinedGroup(userJoinedGroupBO);
        userJoinedGroup.setJoinedAt(CommonUtils.getCurrentISTDateTimeInString());
        UserJoinedGroup createdUserJoinedGroup =  userJoinedGroupDao.createUserJoinedGroup(userJoinedGroup);
        return new UserJoinedGroupBO(createdUserJoinedGroup);
    }

    @Override
    public List<UserJoinedGroupBO> loadUserJoinedGroupByUsername(String username) {
        List<UserJoinedGroup> userJoinedGroupList = userJoinedGroupDao.loadUserJoinedGroupByUsername(username);
        List<UserJoinedGroupBO> userJoinedGroupBOS = new ArrayList<>();
        for (UserJoinedGroup userJoinedGroup : userJoinedGroupList) {
            userJoinedGroupBOS.add(new UserJoinedGroupBO(userJoinedGroup));
        }
        return userJoinedGroupBOS;
    }

    @Override
    public void deleteAllUserJoinedGroup(String username) {
        userJoinedGroupDao.deleteAllUserJoinedGroup(username);
    }

    @Override
    public void deleteUserJoinedGroup(String username, String groupId) {
        userJoinedGroupDao.deleteUserJoinedGroup(username, groupId);
    }
}
