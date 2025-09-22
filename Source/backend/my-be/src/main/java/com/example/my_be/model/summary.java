package com.example.my_be.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "summaries")
public class summary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String summary_id;
    private LocalDateTime approved_at;
    private String content;
    private LocalDateTime created_at;
    private String grade;
    private String image_url;
    private String method;
    private Integer read_count;
    private String status;
    private String summary_content;
    private String title;
    private String created_by;

    public summary() { }
    public String getSummary_id() {
        return summary_id;
    }
    public LocalDateTime getApproved_at() {
        return approved_at;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getCreated_at() {
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
    public Integer getRead_count() {
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
    public void setSummary_id(String summary_id) {
        this.summary_id = summary_id;
    }
    public void setApproved_at(LocalDateTime approved_at) {
        this.approved_at = approved_at;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setCreated_at(LocalDateTime created_at) {
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
    public void setRead_count(Integer read_count) {
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
    public summary(String summary_id, LocalDateTime approved_at, String content, LocalDateTime created_at, String grade, String image_url, String method, Integer read_count, String status, String summary_content, String title, String created_by) {
        this.summary_id = summary_id;
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
