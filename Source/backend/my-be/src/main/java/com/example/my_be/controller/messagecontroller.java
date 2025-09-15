package com.example.my_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_be.dto.request.messagecreationrequest;
import com.example.my_be.entity.message_user_ai;
import com.example.my_be.service.messageservice;

@RestController
@RequestMapping("/message")
public class messagecontroller {
    @Autowired
    private messageservice messageservice;
    @PostMapping
    public message_user_ai createMessage(@RequestBody messagecreationrequest request) {
        return messageservice.createRequest(request);
    }
    @GetMapping
    public List<message_user_ai> getMessages() {
        return messageservice.getMessages();
    }
    @GetMapping("/{message_id}")
    public message_user_ai getMessageById(@PathVariable("message_id") String message_id) {
        return messageservice.getMessageById(message_id);
    }
    @DeleteMapping("/{message_id}")
    public String deleteMessage(@PathVariable("message_id") String message_id) {
        messageservice.deleteMessage(message_id);
        return "Message deleted successfully";
    }
}
