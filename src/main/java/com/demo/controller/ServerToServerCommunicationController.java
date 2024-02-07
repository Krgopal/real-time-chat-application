package com.demo.controller;

import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.ServerToServerMessageDeliveryDetails;
import com.demo.service.ServerToServerCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/v1/api/server")
public class ServerToServerCommunicationController {
    private final ServerToServerCommunicationService serverToServerCommunicationService;

    @Autowired
    public ServerToServerCommunicationController(ServerToServerCommunicationService serverToServerCommunicationService) {
        this.serverToServerCommunicationService = serverToServerCommunicationService;
    }

    @RequestMapping(produces = {"application/json"}, method = RequestMethod.POST)
    public void sendMessageToUsers(@RequestBody ServerToServerMessageDeliveryDetails messageDeliveryDetails) {
        serverToServerCommunicationService.sendMessageToUsers(messageDeliveryDetails);

    }
}
