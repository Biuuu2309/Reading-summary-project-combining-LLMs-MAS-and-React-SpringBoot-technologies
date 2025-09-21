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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/user")
public class usercontroller {
    @Autowired
    private userservice userservice;
    @PostMapping
    public ResponseEntity<user> createUser(@RequestBody usercreationrequest request) {
        return new ResponseEntity<>(userservice.createRequest(request), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<user>> getUsers() {
        return new ResponseEntity<>(userservice.getUsers(), HttpStatus.OK);
    }
    @GetMapping("/{user_id}")
    public ResponseEntity<user> getUserById(@PathVariable("user_id") String user_id) {
        return new ResponseEntity<>(userservice.getUserById(user_id), HttpStatus.OK);
    }
    @PutMapping("/{user_id}")
    public ResponseEntity<user> updateUser(@PathVariable("user_id") String user_id, @RequestBody userupdaterequest request) {
        return new ResponseEntity<>(userservice.updateUser(user_id, request), HttpStatus.OK);
    }
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user_id") String user_id) {
        userservice.deleteUser(user_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
