package com.example.my_be.dto.request;

public class summarycreationrequest {
    public summarycreationrequest() { }
    private String approved_at;
    private String content;
    private String created_at;
    private String grade;
    private String image_url;
    private String method;
    private String read_count;
    private String status;
    private String summary_content;
    private String title;
    private String created_by;

    public String getApproved_at() {
        return approved_at;
    }
    public String getContent() {
        return content;
    }
    public String getCreated_at() {
        return created_at;
    }
    public String getGrade() {
        return grade;
    }
    public String getImage_url() {
        return image_url;
    }
    public String getMethod() {
        return method;
    }
    public String getRead_count() {
        return read_count;
    }
    public String getStatus() {
        return status;
    }
    public String getSummary_content() {
        return summary_content;
    }
    public String getTitle() {
        return title;
    }
    public String getCreated_by() {
        return created_by;
    }
    public void setApproved_at(String approved_at) {
        this.approved_at = approved_at;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public void setRead_count(String read_count) {
        this.read_count = read_count;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setSummary_content(String summary_content) {
        this.summary_content = summary_content;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
    public summarycreationrequest(String approved_at, String content, String created_at, String grade, String image_url, String method, String read_count, String status, String summary_content, String title, String created_by) {
        this.approved_at = approved_at;
        this.content = content;
        this.created_at = created_at;
        this.grade = grade;
        this.image_url = image_url;
        this.method = method;
        this.read_count = read_count;
        this.status = status;
        this.summary_content = summary_content;
        this.title = title;
        this.created_by = created_by;
    }
}
