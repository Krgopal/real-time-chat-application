package com.demo.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ServerToServerMessageDeliveryDetails {
    private ChatMessageDetails chatMessageDetails;
    private Set<String> receivers;
}
