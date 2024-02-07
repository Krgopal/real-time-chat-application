package com.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageHistoryRequest {
    private String senderId;
    private String receiverId;
    private String groupId;
}
