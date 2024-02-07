package com.demo.service;

import com.demo.business.UserJoinedGroupBO;

import java.util.List;

public interface UserJoinedGroupService {
    UserJoinedGroupBO createUserJoinedGroup(UserJoinedGroupBO userJoinedGroupBO);
    List<UserJoinedGroupBO> loadUserJoinedGroupByUsername(String username);
    void deleteAllUserJoinedGroup(String username);
    void deleteUserJoinedGroup(String username, String groupId);
}
