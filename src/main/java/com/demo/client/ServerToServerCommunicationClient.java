package com.demo.client;

import com.demo.model.ServerToServerMessageDeliveryDetails;

public interface ServerToServerCommunicationClient {
    boolean sendRequestToServerForMessageDelivery(String serverId, ServerToServerMessageDeliveryDetails messageDetails);
}
