package com.example.my_be.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MasChatRequest {
    private String sessionId;
    private String userId;
    private String userInput;
    private String conversationId;
    private String imageBase64;
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
    public String getSessionId() {
        return sessionId;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserInput() {
        return userInput;
    }
    public String getConversationId() {
        return conversationId;
    }
    public String getImageBase64() {
        return imageBase64;
    }
}
