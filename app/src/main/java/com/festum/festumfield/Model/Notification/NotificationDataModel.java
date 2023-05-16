package com.festum.festumfield.Model.Notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationDataModel {
    @SerializedName("docs")
    @Expose
    private List<NotificationDetailsModel> docs = null;
    @SerializedName("totalDocs")
    @Expose
    private Integer totalDocs;
    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("pagingCounter")
    @Expose
    private Integer pagingCounter;
    @SerializedName("hasPrevPage")
    @Expose
    private Boolean hasPrevPage;
    @SerializedName("hasNextPage")
    @Expose
    private Boolean hasNextPage;
    @SerializedName("prevPage")
    @Expose
    private Object prevPage;
    @SerializedName("nextPage")
    @Expose
    private Object nextPage;

    public List<NotificationDetailsModel> getDocs() {
        return docs;
    }

    public void setDocs(List<NotificationDetailsModel> docs) {
        this.docs = docs;
    }

    public Integer getTotalDocs() {
        return totalDocs;
    }

    public void setTotalDocs(Integer totalDocs) {
        this.totalDocs = totalDocs;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPagingCounter() {
        return pagingCounter;
    }

    public void setPagingCounter(Integer pagingCounter) {
        this.pagingCounter = pagingCounter;
    }

    public Boolean getHasPrevPage() {
        return hasPrevPage;
    }

    public void setHasPrevPage(Boolean hasPrevPage) {
        this.hasPrevPage = hasPrevPage;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public Object getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(Object prevPage) {
        this.prevPage = prevPage;
    }

    public Object getNextPage() {
        return nextPage;
    }

    public void setNextPage(Object nextPage) {
        this.nextPage = nextPage;
    }
}
