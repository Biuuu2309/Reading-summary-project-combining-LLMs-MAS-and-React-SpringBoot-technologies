package com.example.my_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.entity.user;

@Repository
public interface userrepository extends JpaRepository<user, String> {
    
}
