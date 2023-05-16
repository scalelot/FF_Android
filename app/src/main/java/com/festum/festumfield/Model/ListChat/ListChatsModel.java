package com.festum.festumfield.Model.ListChat;

import com.festum.festumfield.Model.SendMessage.SendAllModelData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListChatsModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("from")
    @Expose
    private ListChatFromModel from;
    @SerializedName("to")
    @Expose
    private ListChatToModel to;
    @SerializedName("context")
    @Expose
    private Object context;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("content")
    @Expose
    private SendAllModelData content;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("id")
    @Expose
    private String id1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ListChatFromModel getFrom() {
        return from;
    }

    public void setFrom(ListChatFromModel from) {
        this.from = from;
    }

    public ListChatToModel getTo() {
        return to;
    }

    public void setTo(ListChatToModel to) {
        this.to = to;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public SendAllModelData getSendAllModelData() {
        return content;
    }

    public void setSendAllModelData(SendAllModelData content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }
}
