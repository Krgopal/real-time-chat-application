package com.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.demo.business.UserDetailsBO;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "user_details")
public class UserInfoDetails {
    @DynamoDBHashKey(attributeName = "username")
    private String username;
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBAttribute(attributeName = "email")
    private String email;
    @DynamoDBAttribute(attributeName = "password")
    private String passwordHash;
    @DynamoDBAttribute(attributeName = "created_at")
    private String createdAt;
    @DynamoDBAttribute(attributeName = "updated_at")
    private String updatedAt;

    public UserInfoDetails(UserDetailsBO userDetailsBO) {
        this.name = userDetailsBO.getName();
        this.username = userDetailsBO.getUsername();
        this.email = userDetailsBO.getEmail();
        this.passwordHash = userDetailsBO.getPassword();
    }

}
