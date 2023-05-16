package com.festum.festumfield.Model.ProfilePic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileDataModel {
    @SerializedName("s3_url")
    @Expose
    private String s3Url;
    @SerializedName("Key")
    @Expose
    private String key;

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
