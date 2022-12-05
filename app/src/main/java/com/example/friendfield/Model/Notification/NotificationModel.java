package com.example.friendfield.Model.Notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationModel {
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private NotificationDetailsModel notificationDetailsModel;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("IsSuccess")
    @Expose
    private Boolean isSuccess;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationDetailsModel getNotificationDetailsModel() {
        return notificationDetailsModel;
    }

    public void setNotificationDetailsModel(NotificationDetailsModel notificationDetailsModel) {
        this.notificationDetailsModel = notificationDetailsModel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
