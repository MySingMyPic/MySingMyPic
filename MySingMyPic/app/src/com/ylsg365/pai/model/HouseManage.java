package com.ylsg365.pai.model;

/**
 * Created by lanzhihong on 2015/4/10.
 */
public class HouseManage {
    private String type;
    private String userId;
    private String page;
    private String rows;
    private String rowStart;
    private String status;

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getPage() {
        return page;
    }

    public String getRows() {
        return rows;
    }

    public String getRowStart() {
        return rowStart;
    }

    public String getStatus() {
        return status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public void setRowStart(String rowStart) {
        this.rowStart = rowStart;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
