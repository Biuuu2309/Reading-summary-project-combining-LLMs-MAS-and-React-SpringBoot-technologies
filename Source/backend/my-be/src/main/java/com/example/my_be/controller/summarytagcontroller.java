package com.example.my_be.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_be.entity.summary;
import com.example.my_be.entity.summarytag;
import com.example.my_be.entity.tag;
import com.example.my_be.service.summaryservice;
import com.example.my_be.service.summarytagservice;
import com.example.my_be.service.tagservice;

@RestController
@RequestMapping("/summarytag")
public class summarytagcontroller {
    @Autowired
    private summarytagservice summaryTagService;

    @Autowired
    private summaryservice summaryService;

    @Autowired
    private tagservice tagService;

    // Get all tags for a specific summary
    @GetMapping("/summary/{summaryId}")
    public ResponseEntity<List<summarytag>> getTagsBySummary(@PathVariable String summaryId) {
        Optional<summary> summary = summaryService.getSummaryById(summaryId);
        if (summary.isPresent()) {
            List<summarytag> tags = summaryTagService.getTagsBySummary(summary.get());
            return new ResponseEntity<>(tags, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get all summaries associated with a specific tag
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<summarytag>> getSummariesByTag(@PathVariable String tagId) {
        Optional<tag> tag = tagService.getTagByName(tagId);
        if (tag.isPresent()) {
            List<summarytag> summaries = summaryTagService.getSummariesByTag(tag.get());
            return new ResponseEntity<>(summaries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Create a new association between a summary and a tag
    @PostMapping
    public ResponseEntity<summarytag> createSummaryTag(@RequestBody summarytag summaryTag) {
        summarytag createdAssociation = summaryTagService.createSummaryTag(summaryTag);
        return new ResponseEntity<>(createdAssociation, HttpStatus.CREATED);
    }

    // Delete an association between a summary and a tag by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSummaryAssociation(@PathVariable String id) {
        Optional<summarytag> existingAssociation = summaryTagService.getSummaryTagById(id);
        if (existingAssociation.isPresent()) {
            summaryTagService.deleteSummaryTag(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
