package com.example.my_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_be.dto.request.messagecreationrequest;
import com.example.my_be.model.message_user_ai;
import com.example.my_be.service.messageservice;

@RestController
@RequestMapping("/message")
public class messagecontroller {
    @Autowired
    private messageservice messageservice;
    @PostMapping
    public ResponseEntity<message_user_ai> createMessage(@RequestBody messagecreationrequest request) {
        return new ResponseEntity<>(messageservice.createRequest(request), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<message_user_ai>> getMessages() {
        return new ResponseEntity<>(messageservice.getMessages(), HttpStatus.OK);
    }
    @GetMapping("/{message_id}")
    public ResponseEntity<message_user_ai> getMessageById(@PathVariable("message_id") String message_id) {
        return new ResponseEntity<>(messageservice.getMessageById(message_id), HttpStatus.OK);
    }
    @DeleteMapping("/{message_id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("message_id") String message_id) {
        messageservice.deleteMessage(message_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
