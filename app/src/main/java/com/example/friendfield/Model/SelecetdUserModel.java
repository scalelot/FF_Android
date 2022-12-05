package com.example.friendfield.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class SelecetdUserModel implements Parcelable {
    String username;
    Boolean isSelected = false;

    public SelecetdUserModel(String username) {
        this.username = username;

    }

    protected SelecetdUserModel(Parcel in) {
        username = in.readString();
        byte tmpIsSelected = in.readByte();
        isSelected = tmpIsSelected == 0 ? null : tmpIsSelected == 1;

    }

    public static final Creator<SelecetdUserModel> CREATOR = new Creator<SelecetdUserModel>() {
        @Override
        public SelecetdUserModel createFromParcel(Parcel in) {
            return new SelecetdUserModel(in);
        }

        @Override
        public SelecetdUserModel[] newArray(int size) {
            return new SelecetdUserModel[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeByte((byte) (isSelected == null ? 0 : isSelected ? 1 : 2));
    }
}
