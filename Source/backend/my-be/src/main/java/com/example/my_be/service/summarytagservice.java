package com.example.my_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.model.summary;
import com.example.my_be.model.summarytag;
import com.example.my_be.model.tag;
import com.example.my_be.repository.summarytagrepository;

@Service
public class summarytagservice {
    @Autowired
    private summarytagrepository summaryTagRepository;

    public summarytag createSummaryTag(summarytag summaryTag) {
        return summaryTagRepository.save(summaryTag);
    }

    public List<summarytag> getTagsBySummary(summary summary) {
        return summaryTagRepository.findBySummary(summary);
    }

    public List<summarytag> getSummariesByTag(tag tag) {
        return summaryTagRepository.findByTag(tag);
    }

    public void deleteSummaryTag(String id) {
        summaryTagRepository.deleteById(id);
    }

    public Optional<summarytag> getSummaryTagById(String id) {
        return summaryTagRepository.findById(id);
    }
}
