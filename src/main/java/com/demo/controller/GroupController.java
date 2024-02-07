package com.demo.controller;

import com.demo.business.GroupDetailsBO;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.AddMemberRequest;
import com.demo.model.GroupDetails;
import com.demo.model.RemoveMemberRequest;
import com.demo.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static com.demo.constants.CommonConstants.APPLICATION_JSON;

@Slf4j
@RestController
@RequestMapping("/v1/api/group")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @RequestMapping(produces = (APPLICATION_JSON), method = RequestMethod.POST)
    public GroupDetails createGroup(@RequestBody GroupDetails groupDetails) {
        return groupService.createGroup(groupDetails);
    }

    @RequestMapping(value = "/{groupId}", produces = {"application/json"}, method = RequestMethod.GET)
    public GroupDetailsBO loadGroupByGroupId(@PathVariable String groupId) {
        return groupService.loadGroupDetails(groupId);
    }

    @RequestMapping(produces = {"application/json"}, method = RequestMethod.PUT)
    public GroupDetails updateGroup(@RequestBody GroupDetails groupDetails) {
        return groupService.updateGroup(groupDetails);
    }

    @RequestMapping(value = "/{groupId}", produces = {"application/json"}, method = RequestMethod.DELETE)
    public void deleteGroup(@PathVariable String groupId) {
        groupService.deleteGroup(groupId);
    }

    @RequestMapping(value = "/member/add", produces = {"application/json"}, method = RequestMethod.POST)
    public boolean addMemberToGroup(@RequestBody AddMemberRequest addMemberRequest) {
        return groupService.addMemberToGroup(addMemberRequest);
    }

    @RequestMapping(value = "/member/remove", produces = {"application/json"}, method = RequestMethod.POST)
    public boolean removeMemberToGroup(@RequestBody RemoveMemberRequest removeMemberRequest) {
        return groupService.removeMemberFromGroup(removeMemberRequest);

    }

}
