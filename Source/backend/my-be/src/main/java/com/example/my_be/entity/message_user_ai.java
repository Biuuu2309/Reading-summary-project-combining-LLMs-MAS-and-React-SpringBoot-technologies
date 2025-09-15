package com.example.my_be.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversations")
public class message_user_ai {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String message_id;
    private String user_id;
    private String role;
    private String message;
    private String created_at;

    public message_user_ai() { }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
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
        this.created_at = created_at;
    }
    public String getMessage_id() {
        return message_id;
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

    public message_user_ai(String message_id, String user_id, String role, String message, String created_at) {
        this.message_id = message_id;
        this.role = role;
        this.user_id = user_id;
        this.message = message;
        this.created_at = created_at;
    }
}
