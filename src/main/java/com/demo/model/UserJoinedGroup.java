package com.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.demo.business.UserJoinedGroupBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "user_joined_group")
public class UserJoinedGroup {
    @DynamoDBHashKey(attributeName = "username")
    private String username;
    @DynamoDBRangeKey(attributeName = "group_id")
    @DynamoDBAttribute(attributeName = "group_id")
    private String groupId;
    @DynamoDBAttribute(attributeName = "group_display_name")
    private String groupDisplayName;
    @DynamoDBAttribute(attributeName = "joined_at")
    private String joinedAt;

    public UserJoinedGroup(UserJoinedGroupBO userJoinedGroupBO) {
        this.username = userJoinedGroupBO.getUsername();
        this.groupId = userJoinedGroupBO.getGroupId();
        this.groupDisplayName = userJoinedGroupBO.getGroupDisplayName();
        this.joinedAt = userJoinedGroupBO.getJoinedAt();
    }

}
