package com.example.friendfield.Model.Product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductImagesModel {
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private ImagesModel data;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("IsSuccess")
    @Expose
    private Boolean isSuccess;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ImagesModel getData() {
        return data;
    }

    public void setData(ImagesModel data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
