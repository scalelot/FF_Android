package com.example.friendfield.Model.SendMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendProductModel {
    @SerializedName("productid")
    @Expose
    private String productid;

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }
}
