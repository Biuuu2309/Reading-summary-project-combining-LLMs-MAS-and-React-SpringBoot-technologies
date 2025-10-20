package com.example.demo.controller;

import com.example.demo.model.Summary;
import com.example.demo.model.SummaryTag;
import com.example.demo.model.Tag;
import com.example.demo.service.SummaryService;
import com.example.demo.service.SummaryTagService;
import com.example.demo.service.TagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/summary-tags")
public class SummaryTagController {

    @Autowired
    private SummaryTagService summaryTagService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private TagService tagService;

    // Get all tags for a specific summary
    @GetMapping("/summary/{summaryId}")
    public ResponseEntity<List<SummaryTag>> getTagsBySummary(@PathVariable String summaryId) {
        Optional<Summary> summary = summaryService.getSummaryById(summaryId);
        if (summary.isPresent()) {
            List<SummaryTag> tags = summaryTagService.getTagsBySummary(summary.get());
            return new ResponseEntity<>(tags, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get all summaries associated with a specific tag
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<SummaryTag>> getSummariesByTag(@PathVariable String tagId) {
        Optional<Tag> tag = tagService.getTagByName(tagId);
        if (tag.isPresent()) {
            List<SummaryTag> summaries = summaryTagService.getSummariesByTag(tag.get());
            return new ResponseEntity<>(summaries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Create a new association between a summary and a tag
    @PostMapping
    public ResponseEntity<SummaryTag> createSummaryTag(@RequestBody SummaryTag summaryTag) {
        SummaryTag createdAssociation = summaryTagService.createSummaryTag(summaryTag);
        return new ResponseEntity<>(createdAssociation, HttpStatus.CREATED);
    }

    // Delete an association between a summary and a tag by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSummaryAssociation(@PathVariable String id) {
        Optional<SummaryTag> existingAssociation = summaryTagService.getSummaryTagById(id);
        if (existingAssociation.isPresent()) {
            summaryTagService.deleteSummaryTag(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
