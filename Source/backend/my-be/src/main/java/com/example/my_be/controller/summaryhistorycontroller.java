package com.example.my_be.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.stream.Collectors;
import com.example.my_be.model.user;
import com.example.my_be.model.summarysession;
import com.example.my_be.model.summaryhistory;
import com.example.my_be.service.summaryhistoryservice;
import com.example.my_be.service.summarysessionservice;
import com.example.my_be.service.userservice;
import com.example.my_be.dto.request.summaryhistoryrequest;

@RestController
@RequestMapping("/summaryhistory")
public class summaryhistorycontroller {
    @Autowired
    private summaryhistoryservice summaryHistoryService;

    @Autowired
    private summarysessionservice summarySessionService;

    @Autowired
    private userservice userService;

    @PostMapping("/start-session")
    public ResponseEntity<summaryhistoryrequest> startSession(@RequestBody StartSessionRequest request) {
        Optional<user> createdByOpt = userService.getUserById(request.getUserId());
        if (createdByOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        user createdBy = createdByOpt.get();    
        summarysession session = new summarysession();
        session.setCreatedBy(createdBy);
        session.setContent(request.getContent());
        session.setTimestamp(new Date().toString());
        session.setContentHash(String.valueOf(request.getContent().hashCode()));

        Optional<summarysession> existingSessionOpt = summarySessionService.getSummarySessionByUserAndContent(createdBy, request.getContent());
        summarysession sessionToUse;
        if (existingSessionOpt.isPresent()) {
            sessionToUse = existingSessionOpt.get();
        } else {
            sessionToUse = summarySessionService.createSummarySession(session);
        }

        summaryhistoryrequest historyrequest = summaryHistoryService.createSummaryHistory(
            sessionToUse, 
            request.getMethod(), 
            request.getContent(), 
            request.getMethod().equals("paraphrase") ? request.getGrade() : null
        );
        return ResponseEntity.ok(historyrequest);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartSessionRequest {
        private String user_id;
        private String content;
        private String method;
        private Integer grade;
    }

    @GetMapping("/{history_id}")
    public ResponseEntity<summaryhistoryrequest> getSummaryHistoryById(@PathVariable Long history_id) {
        Optional<summaryhistoryrequest> historyOpt = summaryHistoryService.getSummaryHistoryById(history_id);
        return historyOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{history_id}")
    public ResponseEntity<summaryhistoryrequest> updateSummaryHistory(@PathVariable Long history_id, @RequestBody summaryhistoryrequest updatedHistoryDTO) {
        Optional<summaryhistoryrequest> historyOpt = summaryHistoryService.getSummaryHistoryById(history_id);
        if (historyOpt.isPresent()) {
            summaryhistoryrequest existingHistoryDTO = historyOpt.get();
            existingHistoryDTO.setMethod(updatedHistoryDTO.getMethod());
            existingHistoryDTO.setSummaryContent(updatedHistoryDTO.getSummaryContent());
            existingHistoryDTO.setIs_accepted(updatedHistoryDTO.getIs_accepted());
            summaryhistoryrequest updatedHistoryEntityDTO = summaryHistoryService.updateSummaryHistory(existingHistoryDTO);
            return ResponseEntity.ok(updatedHistoryEntityDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{history_id}")
    public ResponseEntity<Void> deleteSummaryHistory(@PathVariable Long history_id) {
        summaryHistoryService.deleteSummaryHistory(history_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<summaryhistoryrequest>> getHistoriesByUser(@PathVariable String user_id) {
        Optional<user> userOpt = userService.getUserById(user_id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        user user = userOpt.get();
        List<summarysession> sessions = summarySessionService.findSessionsByUser(user);
        List<summaryhistory> histories = sessions.stream()
            .flatMap(session -> summaryHistoryService.findBySession(session).stream())
            .collect(Collectors.toList());
        List<summaryhistoryrequest> historyDTOs = histories.stream()
            .map(summaryHistoryService::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(historyDTOs);
    }

    @PostMapping("/create-summary")
    public ResponseEntity<summaryhistoryrequest> createSummary(@RequestBody CreateSummaryRequest request) {
        Optional<summarysession> sessionOpt = summarySessionService.getSummarySessionById(request.getSessionId());
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session not found");
        }

        summarysession session = sessionOpt.get();
        summaryhistoryrequest historyDTO = summaryHistoryService.createSummaryHistory(
            session, 
            request.getMethod(), 
            request.getContent(), 
            request.getMethod().equals("paraphrase") ? request.getGrade() : null
        );
        return ResponseEntity.ok(historyDTO);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<summaryhistoryrequest>> getHistoriesBySession(@PathVariable Long sessionId) {
        Optional<summarysession> sessionOpt = summarySessionService.getSummarySessionById(sessionId);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        summarysession session = sessionOpt.get();
        List<summaryhistory> histories = summaryHistoryService.findBySession(session);
        List<summaryhistoryrequest> historyDTOs = histories.stream()
            .map(summaryHistoryService::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(historyDTOs);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSummaryRequest {
        private Long session_id;
        private String method;
        private String content;
        private Integer grade;
    }
}
