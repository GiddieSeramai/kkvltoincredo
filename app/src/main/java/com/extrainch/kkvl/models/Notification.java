package com.extrainch.kkvl.models;

public class Notification {


    public String NotificationID;
    public String Message;
    public String Status;
    public String Title;

    public Notification(String notificationID, String message, String status, String title) {
        NotificationID = notificationID;
        Message = message;
        Status = status;
        Title = title;
    }

    public String getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(String notificationID) {
        NotificationID = notificationID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
