package com.example.my_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.model.message_user_ai;

@Repository
public interface messagerepository extends JpaRepository<message_user_ai, String> {
    
}
