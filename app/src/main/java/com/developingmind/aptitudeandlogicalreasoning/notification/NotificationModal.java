package com.developingmind.aptitudeandlogicalreasoning.notification;

public class NotificationModal {
    private String notificationTitle,notificationDescription,notificationImage;
    private String categoryId,division,topicId,url;

    public NotificationModal(String notificationTitle, String notificationDescription,String notificationImage) {
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
        this.notificationImage = notificationImage;
    }

    public NotificationModal(String notificationTitle, String notificationDescription, String notificationImage, String categoryId, String division, String topicId,String url) {
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
        if(notificationImage!=null)
            this.notificationImage = notificationImage;
        else
            this.notificationImage = "";
        this.categoryId = categoryId;
        this.division = division;
        this.topicId = topicId;
        this.url = url;
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

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
