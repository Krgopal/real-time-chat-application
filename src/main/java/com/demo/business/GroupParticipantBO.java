package com.demo.business;

import com.demo.model.GroupParticipant;
import com.demo.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupParticipantBO {
    private String groupId;
    private String username;
    private String displayName;
    private UserType userType;
    private String joinedAt;

    public GroupParticipantBO(GroupParticipant groupParticipant) {
        this.groupId = groupParticipant.getGroupId();
        this.username = groupParticipant.getUsername();
        this.displayName = groupParticipant.getDisplayName();
        this.userType = groupParticipant.getUserType();
        this.joinedAt = groupParticipant.getJoinedAt();
    }
}
