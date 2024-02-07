package com.demo.business;

import com.demo.model.UserJoinedGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinedGroupBO {
    private String username;
    private String groupId;
    private String groupDisplayName;
    private String joinedAt;
    public UserJoinedGroupBO(UserJoinedGroup userJoinedGroup) {
        this.username = userJoinedGroup.getUsername();
        this.groupId = userJoinedGroup.getGroupId();
        this.groupDisplayName = userJoinedGroup.getGroupDisplayName();
        this.joinedAt = userJoinedGroup.getJoinedAt();
    }
}
