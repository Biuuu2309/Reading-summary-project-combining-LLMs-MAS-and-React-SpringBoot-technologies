package com.example.demo.controller;

import com.example.demo.model.Tag;
import com.example.demo.service.TagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    // Get all tags
    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    // Get a specific tag by ID
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable String id) {
        Optional<Tag> tag = tagService.getTagById(id);
        return tag.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get a specific tag by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String name) {
        Optional<Tag> tag = tagService.getTagByName(name);
        return tag.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Create a new tag
    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    // Delete a specific tag by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        Optional<Tag> existingTag = tagService.getTagById(id);
        if (existingTag.isPresent()) {
            tagService.deleteTag(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
