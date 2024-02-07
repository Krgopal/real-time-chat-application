package com.demo.repository;

import com.demo.model.GroupDetails;

public interface GroupDetailsDao {
    GroupDetails createGroup(GroupDetails group);
    GroupDetails loadGroupByGroupId(String groupId);
    GroupDetails updateGroup(GroupDetails group);
    void deleteGroup(String groupId);
}
