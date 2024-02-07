package com.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddMemberRequest {
    private String groupId;
    private String username;
    private UserType type;
    private String addedBy;
}
