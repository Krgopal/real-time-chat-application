package com.demo.utils;

import com.demo.business.GroupParticipantBO;
import com.demo.constants.CommonConstants;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommonUtils {
    public static String generateP2PConversationId(String user1, String user2) {
        List<String> participants = Arrays.asList(user1,user2);
        return participants.stream().sorted().collect(Collectors.joining(""));
    }
    public static Optional<GroupParticipantBO> getGroupParticipantWithUserName(List<GroupParticipantBO> participants, String username) {
        return participants.stream()
                .filter(groupParticipant -> username.equals(groupParticipant.getUsername()))
                .findFirst();
    }
    public static String generatePendingMessageRedisKey(String username) {
        return CommonConstants.PENDING_MESSAGE_REDIS_CONFIG_KEY + username;
    }

    public static String generateUserConnectionRedisKey(String username) {
        return CommonConstants.USER_CONNECTION_REDIS_CONFIG_KEY + username;
    }
    public static String generateCompositeMessageId(BigInteger messageId, String sender, String receiver) {
        return messageId + "_" + sender + "_" + receiver;
    }

    public static String getCurrentISTDateTimeInString() {
        return DateTime.now().withZone(DateTimeZone.forID("Asia/Kolkata")).toString();
    }

}
