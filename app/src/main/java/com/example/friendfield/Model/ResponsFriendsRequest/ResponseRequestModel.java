package com.example.friendfield.Model.ResponsFriendsRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseRequestModel {
    @SerializedName("friendrequestid")
    @Expose
    private String friendrequestid;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("authorized_permissions")
    @Expose
    private AuthorizedPermissionsModel authorizedPermissions;

    public String getFriendrequestid() {
        return friendrequestid;
    }

    public void setFriendrequestid(String friendrequestid) {
        this.friendrequestid = friendrequestid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AuthorizedPermissionsModel getAuthorizedPermissions() {
        return authorizedPermissions;
    }

    public void setAuthorizedPermissions(AuthorizedPermissionsModel authorizedPermissions) {
        this.authorizedPermissions = authorizedPermissions;
    }
}
