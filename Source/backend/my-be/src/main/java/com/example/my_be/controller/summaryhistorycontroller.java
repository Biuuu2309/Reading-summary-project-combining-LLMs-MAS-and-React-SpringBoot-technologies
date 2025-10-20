package com.example.demo.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.SummaryHistoryDTO;
import com.example.demo.model.SummaryHistory;
import com.example.demo.model.SummarySession;
import com.example.demo.model.User;
import com.example.demo.service.SummaryHistoryService;
import com.example.demo.service.SummarySessionService;
import com.example.demo.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/summary-histories")
public class SummaryHistoryController {

    @Autowired
    private SummaryHistoryService summaryHistoryService;

    @Autowired
    private SummarySessionService summarySessionService;

    @Autowired
    private UserService userService;

    @PostMapping("/start-session")
    public ResponseEntity<SummaryHistoryDTO> startSession(@RequestBody StartSessionRequest request) {
        Optional<User> createdByOpt = userService.getUserById(request.getUserId());
        if (createdByOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User createdBy = createdByOpt.get();
        SummarySession session = new SummarySession();
        session.setCreatedBy(createdBy);
        session.setContent(request.getContent());
        session.setTimestamp(new Date().toString());
        session.setContentHash(String.valueOf(request.getContent().hashCode()));

        Optional<SummarySession> existingSessionOpt = summarySessionService.getSummarySessionByUserAndContent(createdBy, request.getContent());
        SummarySession sessionToUse;
        if (existingSessionOpt.isPresent()) {
            sessionToUse = existingSessionOpt.get();
        } else {
            sessionToUse = summarySessionService.createSummarySession(session);
        }

        SummaryHistoryDTO historyDTO = summaryHistoryService.createSummaryHistory(
            sessionToUse, 
            request.getMethod(), 
            request.getContent(), 
            request.getMethod().equals("paraphrase") ? request.getGrade() : null
        );
        return ResponseEntity.ok(historyDTO);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartSessionRequest {
        private String userId;
        private String content;
        private String method;
        private Integer grade;
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<SummaryHistoryDTO> getSummaryHistoryById(@PathVariable Long historyId) {
        Optional<SummaryHistoryDTO> historyOpt = summaryHistoryService.getSummaryHistoryById(historyId);
        return historyOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{historyId}")
    public ResponseEntity<SummaryHistoryDTO> updateSummaryHistory(@PathVariable Long historyId, @RequestBody SummaryHistoryDTO updatedHistoryDTO) {
        Optional<SummaryHistoryDTO> historyOpt = summaryHistoryService.getSummaryHistoryById(historyId);
        if (historyOpt.isPresent()) {
            SummaryHistoryDTO existingHistoryDTO = historyOpt.get();
            existingHistoryDTO.setMethod(updatedHistoryDTO.getMethod());
            existingHistoryDTO.setSummaryContent(updatedHistoryDTO.getSummaryContent());
            existingHistoryDTO.setIsAccepted(updatedHistoryDTO.getIsAccepted());
            SummaryHistoryDTO updatedHistoryEntityDTO = summaryHistoryService.updateSummaryHistory(existingHistoryDTO);
            return ResponseEntity.ok(updatedHistoryEntityDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> deleteSummaryHistory(@PathVariable Long historyId) {
        summaryHistoryService.deleteSummaryHistory(historyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SummaryHistoryDTO>> getHistoriesByUser(@PathVariable String userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        List<SummarySession> sessions = summarySessionService.findSessionsByUser(user);
        List<SummaryHistory> histories = sessions.stream()
            .flatMap(session -> summaryHistoryService.findBySession(session).stream())
            .collect(Collectors.toList());
        List<SummaryHistoryDTO> historyDTOs = histories.stream()
            .map(summaryHistoryService::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(historyDTOs);
    }

    @PostMapping("/create-summary")
    public ResponseEntity<SummaryHistoryDTO> createSummary(@RequestBody CreateSummaryRequest request) {
        Optional<SummarySession> sessionOpt = summarySessionService.getSummarySessionById(request.getSessionId());
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session not found");
        }

        SummarySession session = sessionOpt.get();
        SummaryHistoryDTO historyDTO = summaryHistoryService.createSummaryHistory(
            session, 
            request.getMethod(), 
            request.getContent(), 
            request.getMethod().equals("paraphrase") ? request.getGrade() : null
        );
        return ResponseEntity.ok(historyDTO);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<SummaryHistoryDTO>> getHistoriesBySession(@PathVariable Long sessionId) {
        Optional<SummarySession> sessionOpt = summarySessionService.getSummarySessionById(sessionId);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SummarySession session = sessionOpt.get();
        List<SummaryHistory> histories = summaryHistoryService.findBySession(session);
        List<SummaryHistoryDTO> historyDTOs = histories.stream()
            .map(summaryHistoryService::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(historyDTOs);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSummaryRequest {
        private Long sessionId;
        private String method;
        private String content;
        private Integer grade;
    }
}