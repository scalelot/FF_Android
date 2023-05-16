package com.festum.festumfield.Model.ContactUs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactModel {

    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("contactNo")
    @Expose
    private String contactNo;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("issue")
    @Expose
    private String issue;

    public ContactModel(String fullName, String contactNo, String emailId, String issue) {
        this.fullName = fullName;
        this.contactNo = contactNo;
        this.emailId = emailId;
        this.issue = issue;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
