package com.demo.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.demo.exception.CommonExceptionType;
import com.demo.model.UserInfoDetails;
import com.demo.repository.UserDetailsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserDetailsDaoImpl implements UserDetailsDao {
    private final DynamoDBMapper dynamoDBMapper;
    @Autowired
    public UserDetailsDaoImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }
    public UserInfoDetails createUser(UserInfoDetails user) {
        try {
            dynamoDBMapper.save(user);
            return user;
        } catch (Exception e) {
            LOG.error("Failed to save user details: " + user + " With error: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to persist data in table.");
        }
    }
    public UserInfoDetails loadUserByUsername(String username) {
        try {
            return dynamoDBMapper.load(UserInfoDetails.class, username);
        } catch (Exception e) {
            LOG.error("Failed to load user details for username: " + username + " with error message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to load user from database.");
        }
    }
    public UserInfoDetails updateUser(UserInfoDetails user) {
        try {
            Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
            expectedAttributeValueMap.put("username", new ExpectedAttributeValue(new AttributeValue().withS(user.getUsername())));
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpected(expectedAttributeValueMap);
            dynamoDBMapper.save(user, saveExpression);
            return user;
        } catch (Exception e) {
            LOG.error("Failed to update user: " + user.getUsername() + " with error message: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("User update failed");
        }
    }

    public void deleteUser(String username) {
        try {
            Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
            expectedAttributeValueMap.put("username", new ExpectedAttributeValue(new AttributeValue().withS(username)));
            DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression().withExpected(expectedAttributeValueMap);
            UserInfoDetails user = UserInfoDetails.builder()
                    .username(username)
                    .build();
            dynamoDBMapper.delete(user, deleteExpression);
        } catch (Exception e) {
            LOG.error("Failed to delete user: " + username + " with error message: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("User deletion failed");
        }
    }
}
