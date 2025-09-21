package com.example.my_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.dto.request.usercreationrequest;
import com.example.my_be.dto.request.userupdaterequest;
import com.example.my_be.entity.user;
import com.example.my_be.repository.userrepository;

@Service
public class userservice {
    @Autowired
    private userrepository userrepository;
    public Optional<user> createRequest(usercreationrequest request) {
        user user = new user();
        user.setAvatar_url(request.getAvatar_url());
        user.setEmail(request.getEmail());
        user.setFull_name(request.getFull_name());
        user.setIs_active(request.getIs_active());
        user.setPassword(request.getPassword());
        user.setPhone_number(request.getPhone_number());
        user.setRole(request.getRole());
        user.setUsername(request.getUsername());
        return Optional.of(userrepository.save(user));
    }
    public List<user> getUsers() {
        return userrepository.findAll();
    }
    public Optional<user> getUserById(String user_id) {
        return userrepository.findById(user_id);
    }
    public Optional<user> updateUser(String user_id, userupdaterequest request) {
        user user = getUserById(user_id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar_url(request.getAvatar_url());
        user.setEmail(request.getEmail());
        user.setFull_name(request.getFull_name());
        user.setIs_active(request.getIs_active());
        user.setPassword(request.getPassword());
        user.setPhone_number(request.getPhone_number());
        user.setRole(request.getRole());
        return Optional.of(userrepository.save(user));
    }
    public void deleteUser(String user_id) {
        userrepository.deleteById(user_id);
    }
}
