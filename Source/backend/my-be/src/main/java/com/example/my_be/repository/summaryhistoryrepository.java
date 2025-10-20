package com.example.demo.repository;

import com.example.demo.model.SummaryHistory;
import com.example.demo.model.SummarySession;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryHistoryRepository extends JpaRepository<SummaryHistory, Long> {
    // You can add custom queries if necessary
    List<SummaryHistory> findBySession(SummarySession session); // Add this
}
