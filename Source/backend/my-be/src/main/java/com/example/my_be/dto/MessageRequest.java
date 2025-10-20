package com.example.my_be.dto.request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class messagecreationrequest {
    private String user_id;
    private String role;
    private String message;
    private String created_at;

    public messagecreationrequest() {
        this.created_at = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setCreated_at(String created_at) {
        if (created_at == null || created_at.isEmpty()) {
            this.created_at = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        } else {
            this.created_at = created_at;
        }
    }
    public String getUser_id() {
        return user_id;
    }
    public String getRole() {
        return role;
    }
    public String getMessage() {
        return message;
    }
    public String getCreated_at() {
        return created_at;
    }

    public messagecreationrequest(String role, String message, String created_at) {
        this.role = role;
        this.message = message;
        this.created_at = created_at;
    }
}
