package com.demo.controller;

import com.demo.business.ChatMessageBO;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.MessageDetails;
import com.demo.model.MessageHistoryRequest;
import com.demo.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = "/history", produces = {"application/json"}, method = RequestMethod.POST)
    public List<ChatMessageBO> loadChatHistory(@RequestBody MessageHistoryRequest messageHistoryRequest) {
        return messageService.fetchOlderChatMessages(messageHistoryRequest);
    }

    @RequestMapping(produces = {"application/json"}, method = RequestMethod.POST)
    public void UpdateMessageDetails(@RequestBody MessageDetails messageDetails) {
            messageService.updateMessageDetails(messageDetails);
    }


}
