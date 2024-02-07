package com.demo.client.impl;

import com.google.gson.Gson;
import com.demo.client.ServerToServerCommunicationClient;
import com.demo.config.ApplicationConfig;
import com.demo.constants.CommonConstants;
import com.demo.model.ServerToServerMessageDeliveryDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class ServerToServerCommunicationClientImpl implements ServerToServerCommunicationClient {
    private final HttpClient httpClient;
    private final ApplicationConfig applicationConfig;
    private final Gson gson;

    @Autowired
    public ServerToServerCommunicationClientImpl(HttpClient httpClient, ApplicationConfig applicationConfig, Gson gson){
        this.httpClient = httpClient;
        this.applicationConfig = applicationConfig;
        this.gson = gson;
    }

    public boolean sendRequestToServerForMessageDelivery(String serverId, ServerToServerMessageDeliveryDetails messageDetails) {
        String serverUrl = System.getenv(CommonConstants.SERVER_URL_CONFIG_KEY + serverId) + "/v1/api/server";
        LOG.info("Sending api call to server: " + serverId + " to deliver message id: " + messageDetails.getChatMessageDetails().getMessageId());
        HttpPost postRequest = new HttpPost(serverUrl);
        String token = "Bearer " + applicationConfig.getServerToServerAuthToken();
        postRequest.setHeader("Authorization", token);
        postRequest.setHeader("Content-type", "application/json");
        String requestString = gson.toJson(messageDetails);
        try {
            postRequest.setEntity(new StringEntity(requestString));
            HttpResponse response = httpClient.execute(postRequest);
            if (response == null || response.getEntity() == null || response.getEntity().getContent() == null) {
                LOG.info("send message request failed for request: " + messageDetails + " with empty response.");
                return false;
            }
            if (response.getStatusLine().getStatusCode() == 200) {
                LOG.info("server to server send message request successful with response: " + response.getEntity().getContent().toString());
                return true;
            }
            LOG.error("Failed to send server request for messageId: " + messageDetails.getChatMessageDetails().getMessageId());
            return false;
        } catch (Exception e) {
            LOG.info("send message request failed for request: " + messageDetails + " with Exception message: " + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }
}
