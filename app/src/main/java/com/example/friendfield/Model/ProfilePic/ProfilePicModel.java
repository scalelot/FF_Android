package com.example.friendfield.Model.ProfilePic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePicModel {
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private ProfileDataModel profileDataModel;
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

    public ProfileDataModel getProfileDataModel() {
        return profileDataModel;
    }

    public void setProfileDataModel(ProfileDataModel profileDataModel) {
        this.profileDataModel = profileDataModel;
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
