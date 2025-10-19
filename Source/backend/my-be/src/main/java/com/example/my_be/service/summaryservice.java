package com.example.my_be.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.dto.request.summarycreationrequest;
import com.example.my_be.dto.request.summaryupdaterequest;
import com.example.my_be.model.readhistory;
import com.example.my_be.model.summary;
import com.example.my_be.model.user;
import com.example.my_be.repository.readhistoryrepository;
import com.example.my_be.repository.summaryrepository;
import java.util.stream.Collectors;
@Service
public class summaryservice {
    @Autowired
    private summaryrepository summaryrepository;
    @Autowired
    private userservice userservice;
    @Autowired
    private readhistoryservice readhistoryservice;
    public summary createSummary(summarycreationrequest request) {
        summary summary = new summary();
        // Parse ISO 8601 timestamps (e.g., 2025-09-16T10:30:00Z) to LocalDateTime
        summary.setApproved_at(OffsetDateTime.parse(request.getApproved_at()).toLocalDateTime());
        summary.setContent(request.getContent());
        summary.setCreated_at(OffsetDateTime.parse(request.getCreated_at()).toLocalDateTime());
        summary.setGrade(request.getGrade());
        summary.setImage_url(request.getImage_url());
        summary.setMethod(request.getMethod());
        summary.setRead_count(Integer.valueOf(request.getRead_count()));
        summary.setStatus(request.getStatus());
        summary.setSummary_content(request.getSummary_content());
        summary.setTitle(request.getTitle());
        summary.setCreated_by(request.getCreated_by());
        return summaryrepository.save(summary);
    }
    public List<summary> getSummaries() {
        return summaryrepository.findAll();
    }
    public summary getSummaryById(String summary_id) {
        return summaryrepository.findById(summary_id).orElseThrow(() -> new RuntimeException("Summary not found"));
    }
    public summary updateSummary(String summary_id, summaryupdaterequest request) {
        summary summary = getSummaryById(summary_id);
        summary.setStatus(request.getStatus());
        return summaryrepository.save(summary);
    }
    public void deleteSummary(String summary_id) {
        summaryrepository.deleteById(summary_id);
    }

    public void logReadHistory(user user, summary summary) {
        readhistory readhistory = new readhistory();
        readhistory.setUser(user);
        readhistory.setSummary(summary);
        summaryrepository.save(readhistory);
    }

    public Optional<summary> getSummaryByIdwoStatus(String id) {
        Optional<summary> summary = summaryrepository.getSummaryByIdwoStatus(id);
        // Return summary with the Id not filter anything
        return summary; // No filter, used by admin
        
    }
    public List<summary> getAllSummariesEntities() {
        return summaryrepository.findAll(); // Unchanged for admin
    }

    public List<summary> getSummariesByStatus(String status) {
        return summaryrepository.findByStatus(status); // No filter, used by status endpoint
    }
    public List<summary> getSummariesByContributor(String userId) {
        Optional<user> optionalUser = userservice.getUserById(userId);
        if (optionalUser.isPresent()) {
            List<summary> summaries = summaryrepository.findByCreatedBy(optionalUser.get()); // All statuses
            List<summarycreationrequest> summarycreationrequests = new ArrayList<>();
            for (summary summary : summaries) {
                summarycreationrequest dto = new summarycreationrequest();
                dto.setSummary_id(summary.getSummary_id());
                dto.setTitle(summary.getTitle());
                dto.setContent(summary.getContent());
                dto.setSummary_content(summary.getSummary_content());
                dto.setStatus(summary.getStatus());
                dto.setMethod(summary.getMethod());
                dto.setGrade(summary.getGrade());
                dto.setCreated_at(summary.getCreated_at().toString());
                dto.setApproved_at(summary.getApproved_at().toString());
                dto.setCreated_by(summary.getCreated_by());
                summarycreationrequests.add(dto);
            }
            return summarycreationrequests;
        } else {
            return new ArrayList<>();
        }
    }

    public List<summary> searchSummariesByTitleAndGrade(String searchTerm, String grade) {
        return summaryrepository.findByTitleContainingIgnoreCaseAndGrade(searchTerm, grade).stream()
            .filter(summary -> "APPROVED".equals(summary.getStatus()))
            .collect(Collectors.toList());
    }

    public List<summary> searchSummaries(String searchTerm, String status, String method, String grade) {
        return summaryrepository.findByTitleContainingOrContentContainingOrStatusContainingOrMethodContainingOrGradeContaining(
            searchTerm, searchTerm, status, method, grade
        ).stream()
            .filter(summary -> "APPROVED".equals(summary.getStatus()))
            .collect(Collectors.toList());
    }
    public List<summary> getTop10MostReadSummaries() {
        return summaryrepository.findTop10ByOrderByReadCountDesc().stream()
            .filter(summary -> "APPROVED".equals(summary.getStatus()))
            .collect(Collectors.toList());
    }
    public List<summary> searchSummariesByTitle(String searchTerm) {
        return summaryrepository.findByTitleContainingIgnoreCase(searchTerm).stream()
            .filter(summary -> "APPROVED".equals(summary.getStatus()))
            .collect(Collectors.toList());
    }

    public List<summary> getSummariesByMethod(String method) {
        return summaryrepository.findByMethod(method).stream()
            .filter(summary -> "APPROVED".equals(summary.getStatus()))
            .collect(Collectors.toList());
    }
}
