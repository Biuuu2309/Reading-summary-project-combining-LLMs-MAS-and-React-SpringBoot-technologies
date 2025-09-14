package com.example.my_be.dto.request;

public class usercreationrequest {
    public usercreationrequest() { }
    private String avatar_url;
    private String email;
    private String full_name;
    private boolean is_active;
    private String password;
    private String phone_number;
    private String role;
    private String username;

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public usercreationrequest(String avatar_url, String email, String full_name, boolean is_active, String password, String phone_number, String role, String username) {
        this.avatar_url = avatar_url;
        this.email = email;
        this.full_name = full_name;
        this.is_active = is_active;
        this.password = password;
        this.phone_number = phone_number;
        this.role = role;
        this.username = username;
    }
}
