package com.example.my_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.entity.tag;
import com.example.my_be.repository.tagrepository;

@Service
public class tagservice {
    @Autowired
    private tagrepository tagrepository;
    public Optional<tag> createTag(tag tag) {
        return Optional.of(tagrepository.save(tag));
    }

    public Optional<tag> getTagById(String id) {
        return tagrepository.findById(id);
    }

    public Optional<tag> getTagByName(String name) {
        return tagrepository.findByName(name);
    }

    public List<tag> getTags() {
        return tagrepository.findAll();
    }

    public Optional<tag> getTagById(String id) {
        return tagrepository.findById(id);
    }

    public Optional<tag> getTagByName(String name) {
        return tagrepository.findByName(name);
    }

    public void deleteTag(String id) {
        tagrepository.deleteById(id);
    }
    public Optional<tag> updateTag(String id, tag tag) {
        tag existingTag = getTagById(id).orElseThrow(() -> new RuntimeException("Tag not found"));
        existingTag.setName(tag.getName());
        return Optional.of(tagrepository.save(existingTag));
    }
}
