package com.example.friendfield.Model.SendMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendProductModel {

    @SerializedName("productid")
    @Expose
    private ProductidModel productid;

    public ProductidModel getProductid() {
        return productid;
    }

    public void setProductid(ProductidModel productid) {
        this.productid = productid;
    }

}
