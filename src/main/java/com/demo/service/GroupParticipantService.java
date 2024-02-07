package com.demo.service;

import com.demo.business.GroupParticipantBO;

import java.util.List;

public interface GroupParticipantService {
    GroupParticipantBO createGroupParticipant(GroupParticipantBO groupParticipant);
    List<GroupParticipantBO> loadGroupParticipantByGroupId(String groupId);
    void deleteGroupParticipant(String groupId, String username);
    void deleteAllGroupParticipant(String groupId);
}
