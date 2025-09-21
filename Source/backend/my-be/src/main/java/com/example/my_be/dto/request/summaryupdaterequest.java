package com.example.my_be.dto.request;

public class summaryupdaterequest {
    public summaryupdaterequest() { }
    private String status;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public summaryupdaterequest(String status) {
        this.status = status;
    }
}
