package com.demo.service;

import com.demo.business.UserDetailsBO;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.UserInfoDetails;
import com.demo.repository.UserDetailsDao;
import com.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDetailsDao userDetailsDao;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserJoinedGroupService userJoinedGroupService;
    @Mock
    private GroupParticipantService groupParticipantService;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void test_createUser_success() {
        String username = "test";
        UserDetailsBO userDetailsBO = UserDetailsBO.builder().username(username).password("ABC").build();
        UserInfoDetails userInfoDetails = UserInfoDetails.builder().username(username).build();
        when(userDetailsDao.createUser(Mockito.any(UserInfoDetails.class))).thenReturn(userInfoDetails);
        when(userDetailsDao.loadUserByUsername(username)).thenReturn(null);
        when(jwtService.encodePassword("ABC")).thenReturn("ABC");
        UserDetailsBO response = userServiceImpl.createUser(userDetailsBO);
        Assertions.assertEquals(response.getUsername(), username);
    }

    @Test
    void test_createUser_failure() {
        try {
            String username = "test";
            UserDetailsBO userDetailsBO = UserDetailsBO.builder().username(username).password("ABC").build();
            UserInfoDetails userInfoDetails = UserInfoDetails.builder().username(username).build();
            when(userDetailsDao.loadUserByUsername(username)).thenReturn(userInfoDetails);
            userServiceImpl.createUser(userDetailsBO);
        } catch (CustomException ce) {
            Assertions.assertEquals(ce.getCode(), CommonExceptionType.USER_EXISTS.getException().getCode());
            Assertions.assertEquals(ce.getStatus(), CommonExceptionType.USER_EXISTS.getException().getStatus());
        }
    }

    @Test
    void test_fetchUserByUsername_success() {
        String username = "test";
        UserInfoDetails userInfoDetails = UserInfoDetails.builder().username(username).build();
        when(userDetailsDao.loadUserByUsername(username)).thenReturn(userInfoDetails);
        UserInfoDetails response = userServiceImpl.fetchUserByUsername(username);
        Assertions.assertEquals(response.getUsername(), username);
    }

    @Test
    void test_fetchUserByUsername_failure() {
        try {
            String username = "test";
            UserInfoDetails response = userServiceImpl.fetchUserByUsername(username);
            Assertions.assertEquals(response.getUsername(), username);
        } catch (CustomException ce) {
            Assertions.assertEquals(ce.getCode(), CommonExceptionType.NOT_FOUND.getException().getCode());
            Assertions.assertEquals(ce.getStatus(), CommonExceptionType.NOT_FOUND.getException().getStatus());
        }
    }
}
