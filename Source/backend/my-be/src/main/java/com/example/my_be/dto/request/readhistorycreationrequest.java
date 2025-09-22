package com.example.my_be.dto.request;

public class readhistorycreationrequest {
    public readhistorycreationrequest() { }

    private String user_id;
    private String summary_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSummary_id() {
        return summary_id;
    }

    public void setSummary_id(String summary_id) {
        this.summary_id = summary_id;
    }

    public readhistorycreationrequest(String user_id, String summary_id) {
        this.user_id = user_id;
        this.summary_id = summary_id;
    }
}


