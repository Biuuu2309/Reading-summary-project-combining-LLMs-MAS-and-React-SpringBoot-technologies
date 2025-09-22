package com.example.my_be.dto.request;

public class summarytagcreationrequest {
    public summarytagcreationrequest() { }

    private String summary_id;
    private String tag_id;

    public String getSummary_id() {
        return summary_id;
    }

    public void setSummary_id(String summary_id) {
        this.summary_id = summary_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public summarytagcreationrequest(String summary_id, String tag_id) {
        this.summary_id = summary_id;
        this.tag_id = tag_id;
    }
}


