package com.festum.festumfield.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class CreateGroupModel implements Parcelable {
    String username;
    Boolean isSelected = false;
    String checkname;

    public CreateGroupModel() {
    }

    public CreateGroupModel(String username) {
        this.username = username;
    }

    protected CreateGroupModel(Parcel in) {
        username = in.readString();
        byte tmpIsSelected = in.readByte();
        isSelected = tmpIsSelected == 0 ? null : tmpIsSelected == 1;
        checkname = in.readString();
    }

    public static final Creator<CreateGroupModel> CREATOR = new Creator<CreateGroupModel>() {
        @Override
        public CreateGroupModel createFromParcel(Parcel in) {
            return new CreateGroupModel(in);
        }

        @Override
        public CreateGroupModel[] newArray(int size) {
            return new CreateGroupModel[size];
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

    public String getCheckname() {
        return checkname;
    }

    public void setCheckname(String checkname) {
        this.checkname = checkname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeByte((byte) (isSelected == null ? 0 : isSelected ? 1 : 2));
        dest.writeString(checkname);
    }
}
