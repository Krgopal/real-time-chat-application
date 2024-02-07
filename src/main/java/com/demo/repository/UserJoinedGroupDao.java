package com.demo.repository;

import com.demo.model.UserJoinedGroup;

import java.util.List;

public interface UserJoinedGroupDao {
    UserJoinedGroup createUserJoinedGroup(UserJoinedGroup userJoinedGroup);
    List<UserJoinedGroup> loadUserJoinedGroupByUsername(String username);
    void deleteAllUserJoinedGroup(String username);
    void deleteUserJoinedGroup(String username, String groupId);
}
