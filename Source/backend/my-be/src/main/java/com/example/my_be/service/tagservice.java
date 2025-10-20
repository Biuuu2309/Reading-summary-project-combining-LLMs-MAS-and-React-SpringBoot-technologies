package com.example.demo.service;

import com.example.demo.model.Tag;
import com.example.demo.repository.TagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    /**
     * Create a new tag.
     */
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    /**
     * Get all tags.
     */
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    /**
     * Get a tag by ID.
     */
    public Optional<Tag> getTagById(String id) {
        return tagRepository.findById(id);
    }

    /**
     * Get a tag by name.
     */
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    /**
     * Delete a tag by ID.
     */
    public void deleteTag(String id) {
        tagRepository.deleteById(id);
    }
}
