package com.example.friendfield.Model.BusinessInfo;

import com.example.friendfield.Model.BrochurePDF.BrochureRegisterModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessInfoModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("businessimage")
    @Expose
    private String businessimage;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("subCategory")
    @Expose
    private String subCategory;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("location")
    @Expose
    private BussinessLocationModel location;
    @SerializedName("interestedCategory")
    @Expose
    private String interestedCategory;
    @SerializedName("interestedSubCategory")
    @Expose
    private String interestedSubCategory;
    @SerializedName("userid")
    @Expose
    private String userid;
    BrochureRegisterModel brochureRegisterModel;
    @SerializedName("brochure")
    @Expose
    private String brochure;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("updatedBy")
    @Expose
    private String updatedBy;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessimage() {
        return businessimage;
    }

    public void setBusinessimage(String businessimage) {
        this.businessimage = businessimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BussinessLocationModel getLocation() {
        return location;
    }

    public void setLocation(BussinessLocationModel location) {
        this.location = location;
    }

    public String getInterestedCategory() {
        return interestedCategory;
    }

    public void setInterestedCategory(String interestedCategory) {
        this.interestedCategory = interestedCategory;
    }

    public String getInterestedSubCategory() {
        return interestedSubCategory;
    }

    public void setInterestedSubCategory(String interestedSubCategory) {
        this.interestedSubCategory = interestedSubCategory;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public BrochureRegisterModel getBrochureRegisterModel() {
        return brochureRegisterModel;
    }

    public void setBrochureRegisterModel(BrochureRegisterModel brochureRegisterModel) {
        this.brochureRegisterModel = brochureRegisterModel;
    }

    public String getBrochure() {
        return brochure;
    }

    public void setBrochure(String brochure) {
        this.brochure = brochure;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

}
