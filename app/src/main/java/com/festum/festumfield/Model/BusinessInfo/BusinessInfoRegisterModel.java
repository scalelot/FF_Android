package com.festum.festumfield.Model.BusinessInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessInfoRegisterModel {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private BusinessInfoModel data;
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

    public BusinessInfoModel getData() {
        return data;
    }

    public void setData(BusinessInfoModel data) {
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
