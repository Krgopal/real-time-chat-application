package com.demo.business;

import com.demo.model.GroupDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailsBO {
    private String id;
    private String name;
    private String createdBy;
    private List<GroupParticipantBO> members;
    private String createdAt;
    private String updatedAt;

    public GroupDetailsBO(GroupDetails groupDetails) {
        this.id = groupDetails.getId();
        this.name = groupDetails.getName();
        this.createdBy = groupDetails.getCreatedBy();
        this.createdAt = groupDetails.getCreatedAt();
        this.updatedAt = groupDetails.getUpdatedAt();
    }

}
