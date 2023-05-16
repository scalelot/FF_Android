package com.festum.festumfield.Model.ListChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListChatFromModel {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("profileimage")
    @Expose
    private String profileimage;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("contact_no")
    @Expose
    private String contactNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
