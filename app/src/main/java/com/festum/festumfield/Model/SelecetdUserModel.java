package com.festum.festumfield.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class SelecetdUserModel implements Parcelable {

    String userName;
    public boolean isSelected = false;

    public SelecetdUserModel(String userName) {
        this.userName = userName;
    }

    protected SelecetdUserModel(Parcel in) {
        userName = in.readString();
        isSelected = in.readByte() != 0;
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

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
