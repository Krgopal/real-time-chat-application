package com.demo.controller;

import com.demo.business.UserDetailsBO;
import com.demo.exception.CommonExceptionType;
import com.demo.service.JwtService;
import com.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private JwtService JwtService;


    @Test
    void test_createUser_Success() throws Exception {
        UserDetailsBO userDetailsBO = new UserDetailsBO();
        userDetailsBO.setUsername("test");
        Mockito.when(userService.createUser(Mockito.any(UserDetailsBO.class))).thenReturn(userDetailsBO);
        ResultActions response = mockMvc.perform(post("/v1/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsBO)));
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(userDetailsBO.getUsername())));
    }

    @Test
    void test_createUser_failure() throws Exception {
        UserDetailsBO userDetailsBO = new UserDetailsBO();
        userDetailsBO.setUsername("test");
        Mockito.when(userService.createUser(Mockito.any(UserDetailsBO.class))).thenThrow(CommonExceptionType.USER_EXISTS.getException("user exists"));
        ResultActions response = mockMvc.perform(post("/v1/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsBO)));
        response.andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void test_loadUserByUsername_Success() throws Exception {
        String username = "test";
        UserDetailsBO userDetailsBO = new UserDetailsBO();
        userDetailsBO.setUsername(username);
        Mockito.when(userService.findUserByUsername(Mockito.any(String.class))).thenReturn(userDetailsBO);
        ResultActions response = mockMvc.perform(get("/v1/api/user/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"));
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(userDetailsBO.getUsername())));
    }

    @Test
    void test_loadUserByUsername_failure() throws Exception {
        String username = "test";
        Mockito.when(userService.findUserByUsername(Mockito.any(String.class))).thenThrow(CommonExceptionType.NOT_FOUND.getException("not found"));
        ResultActions response = mockMvc.perform(get("/v1/api/user/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"));
        response.andExpect(status().is(HttpStatus.SC_NOT_FOUND));
    }

}
