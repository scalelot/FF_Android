package com.festum.festumfield.Model;

import android.os.Parcel;

import java.io.Serializable;

public class ImageSliderModel implements Serializable {

    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ImageSliderModel(String image) {
        this.image = image;
    }

    protected ImageSliderModel(Parcel in) {
        image = in.readString();
    }

}
