package com.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UndeliveredMessage {
    private ChatMessageDetails chatMessageDetails;
    private String receiver;
}
