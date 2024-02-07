package com.demo.service;

import com.demo.business.GroupDetailsBO;
import com.demo.model.AddMemberRequest;
import com.demo.model.GroupDetails;
import com.demo.model.RemoveMemberRequest;

public interface GroupService {
    GroupDetails createGroup(GroupDetails groupDetails);
    GroupDetails updateGroup(GroupDetails groupDetails);
    GroupDetailsBO loadGroupDetails(String groupId);
    void deleteGroup(String groupId);
    boolean addMemberToGroup(AddMemberRequest addMemberRequest);
    boolean removeMemberFromGroup(RemoveMemberRequest removeMemberRequest);
    boolean removeMemberFromGroupWhenUserDeleted(String groupId, String username);
}
