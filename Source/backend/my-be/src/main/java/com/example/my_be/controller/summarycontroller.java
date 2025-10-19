package com.example.my_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_be.dto.request.summarycreationrequest;
import com.example.my_be.dto.request.summaryupdaterequest;
import com.example.my_be.model.summary;
import com.example.my_be.service.summaryservice;

@RestController
@RequestMapping("/summary")
public class summarycontroller {
    @Autowired
    private summaryservice summaryservice;
    @PostMapping
    public ResponseEntity<summary> createSummary(@RequestBody summarycreationrequest request) {
        return new ResponseEntity<>(summaryservice.createSummary(request), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<summary>> getSummaries() {
        return new ResponseEntity<>(summaryservice.getSummaries(), HttpStatus.OK);
    }
    @GetMapping("/{summary_id}")
    public ResponseEntity<summary> getSummaryById(@PathVariable("summary_id") String summary_id) {
        return new ResponseEntity<>(summaryservice.getSummaryById(summary_id), HttpStatus.OK);
    }
    @PutMapping("/{summary_id}/status")
    public ResponseEntity<summary> updateSummary(@PathVariable("summary_id") String summary_id, @RequestBody summaryupdaterequest request) {
        return new ResponseEntity<>(summaryservice.updateSummary(summary_id, request), HttpStatus.OK);
    }
    @DeleteMapping("/{summary_id}")
    public ResponseEntity<Void> deleteSummary(@PathVariable("summary_id") String summary_id) {
        summaryservice.deleteSummary(summary_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/top10")
public ResponseEntity<List<summarydto>> getTop10MostReadSummaries() {
    List<summary> topSummaries = summaryservice.getTop10MostReadSummaries();
    
    // Map the entities to DTOs
    return new ResponseEntity<>(topSummaries, HttpStatus.OK);
}
}