package com.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserConnectionDetails {
    private String username;
    private String serverId;
}
