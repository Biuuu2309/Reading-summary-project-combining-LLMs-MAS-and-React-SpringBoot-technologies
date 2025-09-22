package com.example.my_be.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.dto.request.summarycreationrequest;
import com.example.my_be.dto.request.summaryupdaterequest;
import com.example.my_be.model.summary;
import com.example.my_be.repository.summaryrepository;

@Service
public class summaryservice {
    @Autowired
    private summaryrepository summaryrepository;
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
}
