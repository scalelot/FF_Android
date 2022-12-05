package com.example.friendfield.Model.CreateMessage;

import com.example.friendfield.Model.ChatUserProfile.ChatUserProfileModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateMessageTokenModel {


    @SerializedName("statusCode")
    @Expose
    private String statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private CreateMessageModel createMessageModel;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CreateMessageModel getCreateMessageModel() {
        return createMessageModel;
    }

    public void setCreateMessageModel(CreateMessageModel createMessageModel) {
        this.createMessageModel = createMessageModel;
    }
}
