package com.example.friendfield.Model.ResponsFriendsRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorizedPermissionsModel {
    @SerializedName("fullname")
    @Expose
    private Boolean fullname;
    @SerializedName("contactnumber")
    @Expose
    private Boolean contactnumber;
    @SerializedName("email")
    @Expose
    private Boolean email;
    @SerializedName("dob")
    @Expose
    private Boolean dob;
    @SerializedName("gender")
    @Expose
    private Boolean gender;
    @SerializedName("socialmedia")
    @Expose
    private Boolean socialmedia;
    @SerializedName("videocall")
    @Expose
    private Boolean videocall;
    @SerializedName("audiocall")
    @Expose
    private Boolean audiocall;

    public Boolean getFullname() {
        return fullname;
    }

    public void setFullname(Boolean fullname) {
        this.fullname = fullname;
    }

    public Boolean getContactnumber() {
        return contactnumber;
    }

    public void setContactnumber(Boolean contactnumber) {
        this.contactnumber = contactnumber;
    }

    public Boolean getEmail() {
        return email;
    }

    public void setEmail(Boolean email) {
        this.email = email;
    }

    public Boolean getDob() {
        return dob;
    }

    public void setDob(Boolean dob) {
        this.dob = dob;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Boolean getSocialmedia() {
        return socialmedia;
    }

    public void setSocialmedia(Boolean socialmedia) {
        this.socialmedia = socialmedia;
    }

    public Boolean getVideocall() {
        return videocall;
    }

    public void setVideocall(Boolean videocall) {
        this.videocall = videocall;
    }

    public Boolean getAudiocall() {
        return audiocall;
    }

    public void setAudiocall(Boolean audiocall) {
        this.audiocall = audiocall;
    }

}
