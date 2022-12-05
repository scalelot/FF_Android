package com.example.friendfield.Model.SendMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMediaModel {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("mime")
    @Expose
    private String mime;
    @SerializedName("name")
    @Expose
    private String name;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
