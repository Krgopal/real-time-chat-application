package com.demo.model;

import com.demo.business.ChatMessageBO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PendingMessage {
    private String username;
    private List<ChatMessageDetails> messages;
}
