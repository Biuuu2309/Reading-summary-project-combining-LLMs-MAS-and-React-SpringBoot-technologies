package com.example.my_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.dto.request.summarycreationrequest;
import com.example.my_be.dto.request.summaryupdaterequest;
import com.example.my_be.entity.summary;
import com.example.my_be.repository.summaryrepository;

@Service
public class summaryservice {
    @Autowired
    private summaryrepository summaryrepository;
    public Optional<summary> createSummary(summarycreationrequest request) {
        summary summary = new summary();
        summary.setApproved_at(request.getApproved_at());
        summary.setContent(request.getContent());
        summary.setCreated_at(request.getCreated_at());
        summary.setGrade(request.getGrade());
        summary.setImage_url(request.getImage_url());
        summary.setMethod(request.getMethod());
        summary.setRead_count(request.getRead_count());
        summary.setStatus(request.getStatus());
        summary.setSummary_content(request.getSummary_content());
        summary.setTitle(request.getTitle());
        summary.setCreated_by(request.getCreated_by());
        return Optional.of(summaryrepository.save(summary));
    }
    public List<summary> getSummaries() {
        return summaryrepository.findAll();
    }
    public Optional<summary> getSummaryById(String summary_id) {
        return summaryrepository.findById(summary_id);
    }
    public Optional<summary> updateSummary(String summary_id, summaryupdaterequest request) {
        summary summary = getSummaryById(summary_id).orElseThrow(() -> new RuntimeException("Summary not found"));
        summary.setStatus(request.getStatus());
        return Optional.of(summaryrepository.save(summary));
    }
    public void deleteSummary(String summary_id) {
        summaryrepository.deleteById(summary_id);
    }
}
