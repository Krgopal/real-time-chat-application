package com.demo.controller;

import com.demo.business.UserDetailsBO;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/v1/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register", produces = {"application/json"}, method = RequestMethod.POST)
    public UserDetailsBO createUser(@RequestBody UserDetailsBO userDetailsBO) {
        return userService.createUser(userDetailsBO);
    }

    @RequestMapping(value = "/{username}", produces = {"application/json"}, method = RequestMethod.GET)
    public UserDetailsBO loadUserByUsername(@PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    @RequestMapping(produces = {"application/json"}, method = RequestMethod.PUT)
    public UserDetailsBO updateUser(@RequestBody UserDetailsBO userDetailsBO) {
        return userService.updateUser(userDetailsBO);
    }

    @RequestMapping(value = "/{username}", produces = {"application/json"}, method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }
}
