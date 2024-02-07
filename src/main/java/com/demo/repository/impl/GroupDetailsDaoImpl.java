package com.demo.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.demo.exception.CommonExceptionType;
import com.demo.model.GroupDetails;
import com.demo.repository.GroupDetailsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class GroupDetailsDaoImpl implements GroupDetailsDao {
    private final DynamoDBMapper dynamoDBMapper;
    @Autowired
    public GroupDetailsDaoImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }
    public GroupDetails createGroup(GroupDetails group) {
        try {
            dynamoDBMapper.save(group);
            return group;
        } catch (Exception e) {
            LOG.error("Failed to create group: " + group + " with exception message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to create group");
        }
    }

    public GroupDetails loadGroupByGroupId(String groupId) {
        try {
            return dynamoDBMapper.load(GroupDetails.class, groupId);
        } catch (Exception e) {
            LOG.error("Failed to load group details for groupId: " + groupId + " with exception message: "
                    + e.getMessage() +  " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to load group details");
        }
    }

    public GroupDetails updateGroup(GroupDetails group) {
        try {
            Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
            expectedAttributeValueMap.put("id", new ExpectedAttributeValue(new AttributeValue().withS(group.getId())));
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpected(expectedAttributeValueMap);
            dynamoDBMapper.save(group, saveExpression);
            return group;
        } catch (Exception e) {
            LOG.error("Failed to update group details for groupId: " + group.getId() + " with error message: "
                    + e.getMessage() + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to update Group details");
        }
    }

    public void deleteGroup(String groupId) {
        try {
            Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
            expectedAttributeValueMap.put("id", new ExpectedAttributeValue(new AttributeValue().withS(groupId)));
            DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression().withExpected(expectedAttributeValueMap);
            GroupDetails user = GroupDetails.builder()
                    .id(groupId)
                    .build();
            dynamoDBMapper.delete(user, deleteExpression);
        } catch (Exception e) {
            LOG.error("Failed to delete group with groupId: " + groupId + " with error message: " + e.getMessage()
                    + " and Exception: " + Arrays.toString(e.getStackTrace()));
        }
    }
}
