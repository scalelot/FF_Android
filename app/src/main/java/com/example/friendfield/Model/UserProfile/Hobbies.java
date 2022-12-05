package com.example.friendfield.Model.UserProfile;

import static com.googlecode.mp4parser.h264.Debug.trace;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class Hobbies {

    String name;
    JSONObject hname;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("hobby")
    @Expose
    private String hobby;
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

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
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

    public Hobbies(JSONObject hname) {
        this.hname = hname;
    }

    public Hobbies(String name) {
        this.name = name;
    }

    public JSONObject getHname() {
        return hname;
    }

    public void setHname(JSONObject hname) {
        this.hname = hname;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("hobby", name);
        } catch (JSONException e) {
            trace("DefaultListItem.toString JSONException: " + e.getMessage());
        }
        return obj;
    }
}
