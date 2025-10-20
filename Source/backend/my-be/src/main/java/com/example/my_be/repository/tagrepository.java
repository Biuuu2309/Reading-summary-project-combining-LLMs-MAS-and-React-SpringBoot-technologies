package com.example.demo.repository;

import com.example.demo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    Optional<Tag> findByName(String name); // Find a tag by its unique name
}
