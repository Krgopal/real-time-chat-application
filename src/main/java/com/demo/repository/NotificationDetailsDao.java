package com.demo.repository;

import com.demo.model.NotificationDetails;

public interface NotificationDetailsDao {
    NotificationDetails createNotificationDetails(NotificationDetails notificationDetails);
    NotificationDetails updateNotificationDetails(NotificationDetails notificationDetails);
}
