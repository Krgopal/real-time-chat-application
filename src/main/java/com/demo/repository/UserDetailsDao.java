package com.demo.repository;

import com.demo.model.UserInfoDetails;

public interface UserDetailsDao {
    UserInfoDetails createUser(UserInfoDetails user);
    UserInfoDetails loadUserByUsername(String username);
    UserInfoDetails updateUser(UserInfoDetails user);
    void deleteUser(String username);
}
