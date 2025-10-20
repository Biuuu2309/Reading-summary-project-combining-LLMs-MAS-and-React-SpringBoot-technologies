package com.example.demo.repository;

import com.example.demo.model.ReadHistory;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadHistoryRepository extends JpaRepository<ReadHistory, Long> {

    // Method to find read history by user
    List<ReadHistory> findByUser(User user);

}
