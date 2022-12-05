package com.example.friendfield.Model.SendMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendAllModelData {
    @SerializedName("text")
    @Expose
    private SendTextModel text;
    @SerializedName("media")
    @Expose
    private SendMediaModel media;
    @SerializedName("product")
    @Expose
    private SendProductModel product;

    public SendTextModel getText() {
        return text;
    }

    public void setText(SendTextModel text) {
        this.text = text;
    }

    public SendMediaModel getMedia() {
        return media;
    }

    public void setMedia(SendMediaModel media) {
        this.media = media;
    }

    public SendProductModel getProduct() {
        return product;
    }

    public void setProduct(SendProductModel product) {
        this.product = product;
    }

}
