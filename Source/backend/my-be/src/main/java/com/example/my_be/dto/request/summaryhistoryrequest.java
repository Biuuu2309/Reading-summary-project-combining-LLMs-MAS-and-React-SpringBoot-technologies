package com.example.my_be.dto.request;

public class summaryhistoryrequest {
    private Long history_id;
    private String method;
    private String summaryContent;
    private Boolean is_accepted;
    private Long session_id; // Add this
    private String timestamp; // Add this

    public Long getSession_id() {
        return session_id;
    }

    public void setSession_id(Long session_id) {
        this.session_id = session_id;
    }
    
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public Long getHistory_id() {
        return history_id;
    }

    public void setHistory_id(Long history_id) {
        this.history_id = history_id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSummaryContent() {
        return summaryContent;
    }

    public void setSummaryContent(String summaryContent) {
        this.summaryContent = summaryContent;
    }

    public Boolean getIs_accepted() {
        return is_accepted;
    }

    public void setIsAccepted(Boolean is_accepted) {
        this.is_accepted = is_accepted;
    }
}
