package com.example.friendfield.Model.Product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductModel implements Serializable {
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private ProductDetailsModel productDetailsModel;
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

    public ProductDetailsModel getProductDetailsModel() {
        return productDetailsModel;
    }

    public void setProductDetailsModel(ProductDetailsModel productDetailsModel) {
        this.productDetailsModel = productDetailsModel;
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
