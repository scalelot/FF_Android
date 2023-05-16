package com.festum.festumfield.Model.PersonalInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonalInfoModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("contactNo")
    @Expose
    private String contactNo;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("nickName")
    @Expose
    private String nickName;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("areaRange")
    @Expose
    private Integer areaRange;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("targetAudienceAgeMin")
    @Expose
    private Integer targetAudienceAgeMin;
    @SerializedName("targetAudienceAgeMax")
    @Expose
    private Integer targetAudienceAgeMax;
    @SerializedName("isBusinessProfileRegistered")
    @Expose
    private Boolean isBusinessProfileRegistered;
    @SerializedName("isPersonalProfileRegistered")
    @Expose
    private Boolean isPersonalProfileRegistered;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("facebookLink")
    @Expose
    private String facebookLink;
    @SerializedName("instagramLink")
    @Expose
    private String instagramLink;
    @SerializedName("twitterLink")
    @Expose
    private String twitterLink;
    @SerializedName("linkedinLink")
    @Expose
    private String linkedinLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getAreaRange() {
        return areaRange;
    }

    public void setAreaRange(Integer areaRange) {
        this.areaRange = areaRange;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getTargetAudienceAgeMin() {
        return targetAudienceAgeMin;
    }

    public void setTargetAudienceAgeMin(Integer targetAudienceAgeMin) {
        this.targetAudienceAgeMin = targetAudienceAgeMin;
    }

    public Integer getTargetAudienceAgeMax() {
        return targetAudienceAgeMax;
    }

    public void setTargetAudienceAgeMax(Integer targetAudienceAgeMax) {
        this.targetAudienceAgeMax = targetAudienceAgeMax;
    }

    public Boolean getIsBusinessProfileRegistered() {
        return isBusinessProfileRegistered;
    }

    public void setIsBusinessProfileRegistered(Boolean isBusinessProfileRegistered) {
        this.isBusinessProfileRegistered = isBusinessProfileRegistered;
    }

    public Boolean getIsPersonalProfileRegistered() {
        return isPersonalProfileRegistered;
    }

    public void setIsPersonalProfileRegistered(Boolean isPersonalProfileRegistered) {
        this.isPersonalProfileRegistered = isPersonalProfileRegistered;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getLinkedinLink() {
        return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
        this.linkedinLink = linkedinLink;
    }

}
