package com.example.my_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.model.summarysession;
import com.example.my_be.model.user;

@Repository
public interface summarysessionrepository extends JpaRepository<summarysession, String> {
    Optional<summarysession> findByCreatedByAndContentHash(user createdBy, String contentHash);

    Optional<summarysession> findByCreatedByAndContent(user createdBy, String content);
    List<summarysession> findByCreatedBy(user createdBy); // Add this
}
