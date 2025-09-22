package com.example.my_be.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_be.dto.request.readhistorycreationrequest;
import com.example.my_be.model.readhistory;
import com.example.my_be.model.summary;
import com.example.my_be.model.user;
import com.example.my_be.repository.summaryrepository;
import com.example.my_be.repository.userrepository;
import com.example.my_be.service.readhistoryservice;

@RestController
@RequestMapping("/readhistory")
public class readhistorycontroller {
    @Autowired
    private readhistoryservice readHistoryService;

    @Autowired
    private userrepository userRepository; // To fetch user details by ID

    @Autowired
    private summaryrepository summaryRepository; // To fetch summary details by ID

    // ReadHistoryDTO with added fields for title and imageUrl
    public static class readhistorydto {
        private Long id;
        private String user_id;
        private String summary_id;
        private String title;      // Added field for title
        private String imageUrl;   // Added field for imageUrl
    
        // Constructor
        public readhistorydto(Long id, String user_id, String summary_id, String title, String imageUrl) {
            this.id = id;
            this.user_id = user_id;
            this.summary_id = summary_id;
            this.title = title;
            this.imageUrl = imageUrl;
        }
    
        // Getters and Setters
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }
    
        public String getUser_id() {
            return user_id;
        }
    
        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    
        public String getSummary_id() {
            return summary_id;
        }
    
        public void setSummary_id(String summary_id) {
            this.summary_id = summary_id;
        }
    
        public String getTitle() {
            return title;
        }
    
        public void setTitle(String title) {
            this.title = title;
        }
    
        public String getImageUrl() {
            return imageUrl;
        }
    
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
    

    // Endpoint to log a new read history
    @PostMapping("/log")
    public ResponseEntity<readhistory> logReadHistory(@RequestParam String user_id, @RequestParam String summary_id) {
        // Fetch the user and summary by their IDs
        user user = userRepository.findById(user_id).orElse(null);
        summary summary = summaryRepository.findById(summary_id).orElse(null);

        if (user == null || summary == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // If either user or summary not found
        }

        // Log the read history
        readhistory readHistory = readHistoryService.logReadHistory(user, summary);

        return new ResponseEntity<>(readHistory, HttpStatus.CREATED);
    }

    // JSON variant: accepts application/json body with { user_id, summary_id }
    @PostMapping(value = "/log", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<readhistorydto> logReadHistoryJson(@RequestBody readhistorycreationrequest request) {
        user user = userRepository.findById(request.getUser_id()).orElse(null);
        summary summary = summaryRepository.findById(request.getSummary_id()).orElse(null);
        if (user == null || summary == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        readhistory readHistory = readHistoryService.logReadHistory(user, summary);
        readhistorydto dto = new readhistorydto(
            readHistory.getId(),
            user.getUser_id(),
            summary.getSummary_id(),
            summary.getTitle(),
            summary.getImage_url()
        );
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // Get all ReadHistory by user
    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<readhistorydto>> getReadHistoryByUser(@PathVariable String user_id) {
        user user = userRepository.findById(user_id).orElse(null);
    
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // If user not found
        }
    
        List<readhistory> readHistories = readHistoryService.getReadHistoryByUser(user);
    
        // Convert ReadHistory to ReadHistoryDTO to avoid lazy-loaded fields
        List<readhistorydto> readHistoryDTOs = readHistories.stream()
            .map(readHistory -> {
                // Get the associated Summary
                summary summary = readHistory.getSummary();
                return new readhistorydto(
                    readHistory.getId(),
                    readHistory.getUser().getUser_id(),
                    readHistory.getSummary().getSummary_id(),
                    summary.getTitle(),
                    summary.getImage_url()
                );
            })
            .collect(Collectors.toList());
    
        return new ResponseEntity<>(readHistoryDTOs, HttpStatus.OK);
    }
}
