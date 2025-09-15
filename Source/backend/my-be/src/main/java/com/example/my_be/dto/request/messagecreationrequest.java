package com.example.my_be.dto.request;

public class messagecreationrequest {
    private String role;
    private String message;
    private String created_at;

    public messagecreationrequest() { }

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
