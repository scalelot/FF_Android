package com.example.friendfield.Model.Profile.Register;

import com.example.friendfield.Model.UserProfile.SocialMediaLink;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileModel {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("contact_no")
    @Expose
    private String contactNo;
    @SerializedName("last_sent_otp")
    @Expose
    private String lastSentOtp;
    @SerializedName("otp_timestamp")
    @Expose
    private Long otpTimestamp;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("aboutUs")
    @Expose
    private String aboutUs;
    @SerializedName("areaRange")
    @Expose
    private Integer areaRange;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("hobbies")
    @Expose
    private List<String> hobbies = null;
    @SerializedName("nickName")
    @Expose
    private String nickName;
    @SerializedName("socialMediaLinks")
    @Expose
    private List<SocialMediaLink> socialMediaLinks = null;
    @SerializedName("targetAudienceAgeMax")
    @Expose
    private Integer targetAudienceAgeMax;
    @SerializedName("targetAudienceAgeMin")
    @Expose
    private Integer targetAudienceAgeMin;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("profileimage")
    @Expose
    private String profileimage;
    @SerializedName("location")
    @Expose
    private LocationModel locationModel;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("interestedin")
    @Expose
    private String interestedin;
    @SerializedName("channelID")
    @Expose
    private String channelID;
    @SerializedName("is_business_profile_created")
    @Expose
    private Boolean isBusinessProfileCreated;

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

    public String getLastSentOtp() {
        return lastSentOtp;
    }

    public void setLastSentOtp(String lastSentOtp) {
        this.lastSentOtp = lastSentOtp;
    }

    public Long getOtpTimestamp() {
        return otpTimestamp;
    }

    public void setOtpTimestamp(Long otpTimestamp) {
        this.otpTimestamp = otpTimestamp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public Integer getAreaRange() {
        return areaRange;
    }

    public void setAreaRange(Integer areaRange) {
        this.areaRange = areaRange;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public List<SocialMediaLink> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(List<SocialMediaLink> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    public Integer getTargetAudienceAgeMax() {
        return targetAudienceAgeMax;
    }

    public void setTargetAudienceAgeMax(Integer targetAudienceAgeMax) {
        this.targetAudienceAgeMax = targetAudienceAgeMax;
    }

    public Integer getTargetAudienceAgeMin() {
        return targetAudienceAgeMin;
    }

    public void setTargetAudienceAgeMin(Integer targetAudienceAgeMin) {
        this.targetAudienceAgeMin = targetAudienceAgeMin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public LocationModel getLocationModel() {
        return locationModel;
    }

    public void setLocationModel(LocationModel locationModel) {
        this.locationModel = locationModel;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getInterestedin() {
        return interestedin;
    }

    public void setInterestedin(String interestedin) {
        this.interestedin = interestedin;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public Boolean getIsBusinessProfileCreated() {
        return isBusinessProfileCreated;
    }

    public void setIsBusinessProfileCreated(Boolean isBusinessProfileCreated) {
        this.isBusinessProfileCreated = isBusinessProfileCreated;
    }
}
