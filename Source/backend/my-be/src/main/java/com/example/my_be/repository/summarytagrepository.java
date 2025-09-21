package com.example.my_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.entity.summary;
import com.example.my_be.entity.summarytag;
import com.example.my_be.entity.tag;

@Repository
public interface summarytagrepository extends JpaRepository<summarytag, String> {
    List<summarytag> findBySummary(summary summary); // Find all tags for a specific summary

    List<summarytag> findByTag(tag tag); // Find all summaries associated with a specific tag
}
