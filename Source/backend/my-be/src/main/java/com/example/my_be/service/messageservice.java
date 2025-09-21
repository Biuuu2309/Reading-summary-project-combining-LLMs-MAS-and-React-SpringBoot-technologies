package com.example.my_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.dto.request.messagecreationrequest;
import com.example.my_be.entity.message_user_ai;
import com.example.my_be.repository.messagerepository;

@Service
public class messageservice {
    @Autowired
    private messagerepository messagerepository;
    public Optional<message_user_ai> createRequest(messagecreationrequest request) {
        message_user_ai message_user_ai = new message_user_ai();
        message_user_ai.setUser_id(request.getUser_id());
        message_user_ai.setRole(request.getRole());
        message_user_ai.setMessage(request.getMessage());
        message_user_ai.setCreated_at(request.getCreated_at());
        return Optional.of(messagerepository.save(message_user_ai));
    }
    public List<message_user_ai> getMessages() {
        return messagerepository.findAll();
    }
    public Optional<message_user_ai> getMessageById(String message_id) {
        return messagerepository.findById(message_id);
    }
    public void deleteMessage(String message_id) {
        messagerepository.deleteById(message_id);
    }
}
