package com.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.demo.business.GroupParticipantBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "group_participants")
public class GroupParticipant {
    @DynamoDBHashKey(attributeName = "group_id")
    private String groupId;
    @DynamoDBRangeKey(attributeName = "username")
    @DynamoDBAttribute(attributeName = "username")
    private String username;
    @DynamoDBAttribute(attributeName = "display_name")
    private String displayName;
    @DynamoDBAttribute(attributeName = "userType")
    @DynamoDBTypeConvertedEnum
    private UserType userType;
    @DynamoDBAttribute(attributeName = "joined_at")
    private String joinedAt;

    public GroupParticipant(GroupParticipantBO groupParticipantBO) {
        this.groupId = groupParticipantBO.getGroupId();
        this.username = groupParticipantBO.getUsername();
        this.displayName = groupParticipantBO.getDisplayName();
        this.userType = groupParticipantBO.getUserType();
        this.joinedAt = groupParticipantBO.getJoinedAt();
    }

}
