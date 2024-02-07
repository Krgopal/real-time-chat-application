package com.demo.repository;

import com.demo.model.GroupParticipant;

import java.util.List;

public interface GroupParticipantDao {
    GroupParticipant createGroupParticipant(GroupParticipant groupParticipant);
    List<GroupParticipant> loadGroupParticipantByGroupId(String groupId);
    void deleteGroupParticipant(String groupId, String username);
    void deleteAllGroupParticipant(String groupId);
}
