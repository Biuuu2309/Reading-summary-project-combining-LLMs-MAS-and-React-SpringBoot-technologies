package com.example.my_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.entity.tag;

@Repository
public interface tagrepository extends JpaRepository<tag, String> {
    Optional<tag> findByName(String name);
}
