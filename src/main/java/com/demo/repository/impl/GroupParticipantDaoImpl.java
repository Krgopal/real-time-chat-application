package com.demo.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.demo.exception.CommonExceptionType;
import com.demo.model.GroupParticipant;
import com.demo.model.UserInfoDetails;
import com.demo.model.UserJoinedGroup;
import com.demo.repository.GroupParticipantDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class GroupParticipantDaoImpl implements GroupParticipantDao {
    private final DynamoDBMapper dynamoDBMapper;
    @Autowired
    public GroupParticipantDaoImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }
    public GroupParticipant createGroupParticipant(GroupParticipant groupParticipant) {
        try {
            dynamoDBMapper.save(groupParticipant);
            return groupParticipant;
        } catch (Exception e) {
            LOG.error("Failed to save group participant: " + groupParticipant + " With error: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to persist data in table.");
        }
    }
    public List<GroupParticipant> loadGroupParticipantByGroupId(String groupId) {
        try {
            final GroupParticipant groupParticipant = new GroupParticipant();
            groupParticipant.setGroupId(groupId);
            final DynamoDBQueryExpression<GroupParticipant> queryExpression = new DynamoDBQueryExpression<GroupParticipant>()//
                    .withHashKeyValues(groupParticipant);
            final List<GroupParticipant> storedEntries = dynamoDBMapper.query(GroupParticipant.class, queryExpression);
            if (storedEntries.isEmpty()) {
                return new ArrayList<>();
            }
            return storedEntries;
        } catch (Exception e) {
            LOG.error("Failed to load group participants for groupId: " + groupId + " with error message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to load group participant from database.");
        }
    }
    public void deleteGroupParticipant(String groupId, String username) {
        try {
            Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
            expectedAttributeValueMap.put("group_id", new ExpectedAttributeValue(new AttributeValue().withS(groupId)));
            expectedAttributeValueMap.put("username", new ExpectedAttributeValue(new AttributeValue().withS(username)));
            DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression().withExpected(expectedAttributeValueMap);
            GroupParticipant groupParticipant = GroupParticipant.builder()
                    .username(username)
                    .groupId(groupId)
                    .build();
            dynamoDBMapper.delete(groupParticipant, deleteExpression);
        } catch (Exception e) {
            LOG.error("Failed to delete group participant for: " + groupId + " and username: " + username + " with error message: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("group participant delete failed");
        }
    }

    public void deleteAllGroupParticipant(String groupId) {
        try {

            List<GroupParticipant> groupParticipants = loadGroupParticipantByGroupId(groupId);
            List<DynamoDBMapper.FailedBatch> failedBatch = dynamoDBMapper.batchDelete(groupParticipants);
            System.out.println(failedBatch);
        } catch (Exception e) {
            LOG.error("Failed to delete group participant for group: " + groupId + " with error message: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("User deletion failed");
        }
    }
}
