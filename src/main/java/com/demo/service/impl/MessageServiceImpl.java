package com.demo.service.impl;

import com.demo.business.ChatMessageBO;
import com.demo.business.GroupDetailsBO;
import com.demo.business.GroupParticipantBO;
import com.demo.business.UserDetailsBO;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.*;
import com.demo.repository.MessageDetailsDao;
import com.demo.service.GroupService;
import com.demo.service.MessageService;
import com.demo.service.UserService;
import com.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    private final MessageDetailsDao messageDetailsDao;
    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public MessageServiceImpl(MessageDetailsDao messageDetailsDao, GroupService groupService, UserService userService) {
        this.messageDetailsDao = messageDetailsDao;
        this.groupService = groupService;
        this.userService = userService;
    }

    public List<ChatMessageBO> fetchOlderChatMessages(MessageHistoryRequest messageHistoryRequest) {
        try {
            String conversationId = messageHistoryRequest.getGroupId();
            if (StringUtils.isEmpty(messageHistoryRequest.getGroupId())) {
                conversationId = CommonUtils.generateP2PConversationId(messageHistoryRequest.getSenderId(), messageHistoryRequest.getReceiverId());
            }
            List<MessageDetails> messageDetailsList = messageDetailsDao.fetchHistory(conversationId);
            List<ChatMessageBO> messageHistory = new ArrayList<>();
            for (MessageDetails messageDetails : messageDetailsList) {
                messageHistory.add(new ChatMessageBO(messageDetails));
            }
            return sortMessages(messageHistory);
        } catch (CustomException ce) {
            LOG.error("Failed to load history of user messages for request: " + messageHistoryRequest + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to load history of user message for request: " + messageHistoryRequest + " with error message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException();
        }
    }

    /*
     Sorting messages based on messageId.
     if same messageId then sort based on created time.
     if same time then sort based on username
     */
    public List<ChatMessageBO> sortMessages(List<ChatMessageBO> messageHistory) {
        Comparator<ChatMessageBO> compareByName = Comparator
                .comparing(ChatMessageBO::getMessageId)
                .thenComparing(ChatMessageBO::getCreatedAt)
                .thenComparing(ChatMessageBO::getSenderId).reversed();
        return messageHistory.stream().sorted(compareByName).collect(Collectors.toList());
    }

    private void persistMessage(MessageDetails messageDetails) {
        if (ConversationType.P2P_MESSAGE.equals(messageDetails.getConversationType())) {
            // Checking if user is present with the username or not.
            userService.findUserByUsername(messageDetails.getMessageTo());
        }
        messageDetails.setStatus(MessageStatus.PENDING);
        messageDetailsDao.createMessage(messageDetails);
    }

    public DeliveryMessageDetails setConversationTypeAndConversationId(MessageDetails messageDetails) {
        DeliveryMessageDetails deliveryMessageDetails = new DeliveryMessageDetails();
        try {
            GroupDetailsBO groupDetailsBO = groupService.loadGroupDetails(messageDetails.getMessageTo());
            Optional<GroupParticipantBO> groupParticipant = CommonUtils.getGroupParticipantWithUserName(groupDetailsBO.getMembers(), messageDetails.getSenderId());
            if (groupParticipant.isEmpty()) {
                throw CommonExceptionType.USER_OPERATION_NOT_ALLOWED.getException("Sender: " + messageDetails.getSenderId() + " not part of this group: " + groupDetailsBO.getName());
            }
            messageDetails.setConversationType(ConversationType.GROUP_MESSAGE);
            messageDetails.setConversationId(messageDetails.getMessageTo());
            List<String> receivers = groupDetailsBO.getMembers().stream()
                    .distinct().map(GroupParticipantBO::getUsername)
                    .collect(Collectors.toList());
            deliveryMessageDetails.setReceivers(receivers);
        } catch (CustomException ce) {
            LOG.info("No group found with groupId: " + messageDetails.getMessageTo() + " Sending p2p message to user.");
            UserDetailsBO userDetailsBO = userService.findUserByUsername(messageDetails.getSenderId());
            messageDetails.setConversationType(ConversationType.P2P_MESSAGE);
            messageDetails.setSenderDisplayName(userDetailsBO.getName());
            messageDetails.setConversationId(CommonUtils.generateP2PConversationId(messageDetails.getMessageTo(), messageDetails.getSenderId()));
            deliveryMessageDetails.setReceivers(Collections.singletonList(messageDetails.getMessageTo()));
        }
        messageDetails.setCreatedAt(CommonUtils.getCurrentISTDateTimeInString());
        deliveryMessageDetails.setMessageDetails(messageDetails);
        return deliveryMessageDetails;
    }

    public DeliveryMessageDetails processMessage(ChatMessageBO chatMessageBO) {
        MessageDetails messageDetails = new MessageDetails(chatMessageBO);
        DeliveryMessageDetails deliveryMessageDetails = setConversationTypeAndConversationId(messageDetails);
        persistMessage(deliveryMessageDetails.getMessageDetails());
        return deliveryMessageDetails;
    }

    public void updateMessageDetails(MessageDetails messageDetails) {
        try {
            messageDetailsDao.updateMessageDetails(messageDetails);
        } catch (CustomException ce) {
            LOG.error("Failed to update message details for request: " + messageDetails + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to update message details for request: " + messageDetails + " with error message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException();
        }
    }
}
