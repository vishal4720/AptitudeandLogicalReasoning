package com.developingmind.aptitudeandlogicalreasoning.notification;

public class NotificationModal {
    private String notificationTitle,notificationDescription;

    public NotificationModal(String notificationTitle, String notificationDescription) {
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }
}
