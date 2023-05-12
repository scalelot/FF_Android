package com.example.friendfield.Model.UserProfile;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class SocialMediaLink {
    String splatform;
    String slink;

    public SocialMediaLink(String platform, String link) {
        this.splatform = platform;
        this.slink = link;
    }
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("platform", splatform);
            obj.put("link", slink);
        } catch (JSONException e) {
            e.getMessage();
        }
        return obj;
    }

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("user_id")
    @Expose
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
