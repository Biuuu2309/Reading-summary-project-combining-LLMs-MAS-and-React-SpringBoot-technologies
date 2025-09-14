package com.example.my_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_be.dto.request.usercreationrequest;
import com.example.my_be.dto.request.userupdaterequest;
import com.example.my_be.entity.user;
import com.example.my_be.service.userservice;

@RestController
@RequestMapping("/user")
public class usercontroller {
    @Autowired
    private userservice userservice;
    @PostMapping
    public user createUser(@RequestBody usercreationrequest request) {
        return userservice.createRequest(request);
    }
    @GetMapping
    public List<user> getUsers() {
        return userservice.getUsers();
    }
    @GetMapping("/{user_id}")
    public user getUserById(@PathVariable("user_id") String user_id) {
        return userservice.getUserById(user_id);
    }
    @PutMapping("/{user_id}")
    public user updateUser(@PathVariable("user_id") String user_id, @RequestBody userupdaterequest request) {
        return userservice.updateUser(user_id, request);
    }
    @DeleteMapping("/{user_id}")
    public String deleteUser(@PathVariable("user_id") String user_id) {
        userservice.deleteUser(user_id);
        return "User deleted successfully";
    }
}
