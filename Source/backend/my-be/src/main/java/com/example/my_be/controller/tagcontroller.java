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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_be.model.tag;
import com.example.my_be.service.tagservice;

@RestController
@RequestMapping("/tag")
public class tagcontroller {
    @Autowired
    private tagservice tagservice;
    @GetMapping
    public ResponseEntity<List<tag>> getAllTags() {
        List<tag> tags = tagservice.getTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    // Get a specific tag by ID
    @GetMapping("/{id}")
    public ResponseEntity<tag> getTagById(@PathVariable String id) {
        Optional<tag> tag = tagservice.getTagById(id);
        return tag.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get a specific tag by name
    @GetMapping("/name/{name}")
    public ResponseEntity<tag> getTagByName(@PathVariable String name) {
        Optional<tag> tag = tagservice.getTagByName(name);
        return tag.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Create a new tag
    @PostMapping
    public ResponseEntity<tag> createTag(@RequestBody tag tag) {
        tag createdTag = tagservice.createTag(tag).orElseThrow(() -> new RuntimeException("Tag not created"));
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    // Delete a specific tag by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        Optional<tag> existingTag = tagservice.getTagById(id);
        if (existingTag.isPresent()) {
            tagservice.deleteTag(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/{id}")
    public ResponseEntity<tag> updateTag(@PathVariable String id, @RequestBody tag tag) {
        tag updatedTag = tagservice.updateTag(id, tag).orElseThrow(() -> new RuntimeException("Tag not updated"));
        return new ResponseEntity<>(updatedTag, HttpStatus.OK);
    }
}
