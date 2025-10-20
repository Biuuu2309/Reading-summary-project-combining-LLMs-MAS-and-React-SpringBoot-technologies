package com.example.my_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.dto.MessageRequest;
import com.example.my_be.model.MessageUserAi;
import com.example.my_be.repository.MessageRepository;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messagerepository;
    public MessageUserAi createRequest(MessageRequest request) {
        MessageUserAi message_user_ai = new MessageUserAi();
        message_user_ai.setUser_id(request.getUser_id());
        message_user_ai.setRole(request.getRole());
        message_user_ai.setMessage(request.getMessage());
        message_user_ai.setCreated_at(request.getCreated_at());
        return messagerepository.save(message_user_ai);
    }
    public List<MessageUserAi> getMessages() {
        return messagerepository.findAll();
    }
    public Optional<MessageUserAi> getMessageById(String message_id) {
        return messagerepository.findById(message_id);
    }
    public void deleteMessage(String message_id) {
        messagerepository.deleteById(message_id);
    }
}
