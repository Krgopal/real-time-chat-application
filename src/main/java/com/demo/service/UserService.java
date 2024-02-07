package com.demo.service;

import com.demo.business.UserDetailsBO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetailsBO createUser(UserDetailsBO userInfoDetails);
    UserDetailsBO updateUser(UserDetailsBO userInfoDetails);
    UserDetails loadUserByUsername(String username);
    UserDetailsBO findUserByUsername(String userId);
    void deleteUser(String userId);
}
