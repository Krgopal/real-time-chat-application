package com.demo.service;

import com.demo.model.ServerToServerMessageDeliveryDetails;

public interface ServerToServerCommunicationService {
    void sendMessageToUsers(ServerToServerMessageDeliveryDetails serverToServerMessageDeliveryDetails);
    void sendRequestToServerForMessageDelivery(String serverId, ServerToServerMessageDeliveryDetails messageDetails);
}
