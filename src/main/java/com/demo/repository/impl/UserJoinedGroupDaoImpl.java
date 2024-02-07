package com.demo.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.demo.exception.CommonExceptionType;
import com.demo.model.UserJoinedGroup;
import com.demo.repository.UserJoinedGroupDao;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class UserJoinedGroupDaoImpl implements UserJoinedGroupDao {
    private final DynamoDBMapper dynamoDBMapper;
    @Autowired
    public UserJoinedGroupDaoImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }
    public UserJoinedGroup createUserJoinedGroup(UserJoinedGroup userJoinedGroup) {
        try {
            dynamoDBMapper.save(userJoinedGroup);
            return userJoinedGroup;
        } catch (Exception e) {
            LOG.error("Failed to save user joined group: " + userJoinedGroup + " With error: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to persist data in table.");
        }
    }
    public List<UserJoinedGroup> loadUserJoinedGroupByUsername(String username) {
        try {
            final UserJoinedGroup userJoinedGroup = new UserJoinedGroup();
            userJoinedGroup.setUsername(username);
            final DynamoDBQueryExpression<UserJoinedGroup> queryExpression = new DynamoDBQueryExpression<UserJoinedGroup>()//
                    .withHashKeyValues(userJoinedGroup);
            final List<UserJoinedGroup> storedEntries = dynamoDBMapper.query(UserJoinedGroup.class, queryExpression);
            if (storedEntries.isEmpty()) {
                return new ArrayList<>();
            }
            return storedEntries;
        } catch (Exception e) {
            LOG.error("Failed to load user joined group for username: " + username + " with error message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to load user joined group from database.");
        }
    }
    public void deleteUserJoinedGroup(String username, String groupId) {
        try {
            LOG.info("got request to remove user: " + username +  " from group: " + groupId);
            Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
            expectedAttributeValueMap.put("username", new ExpectedAttributeValue(new AttributeValue().withS(username)));
            expectedAttributeValueMap.put("group_id", new ExpectedAttributeValue(new AttributeValue().withS(groupId)));
            DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression().withExpected(expectedAttributeValueMap);
            UserJoinedGroup userJoinedGroup = UserJoinedGroup.builder()
                    .username(username)
                    .groupId(groupId)
                    .build();
            dynamoDBMapper.delete(userJoinedGroup, deleteExpression);
        } catch (Exception e) {
            LOG.error("Failed to delete user joined group for username: " + username + " and groupId: " + groupId
                    + " with error message: " + e.getMessage() + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("group participant delete failed");
        }
    }

    public void deleteAllUserJoinedGroup(String username) {
        try {
            List<UserJoinedGroup> userJoinedGroups = loadUserJoinedGroupByUsername(username);
            List<DynamoDBMapper.FailedBatch> failedBatch = dynamoDBMapper.batchDelete(userJoinedGroups);
        } catch (Exception e) {
            LOG.error("Failed to delete user joined group for username: " + username + " with error message: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("User deletion failed");
        }
    }
}
