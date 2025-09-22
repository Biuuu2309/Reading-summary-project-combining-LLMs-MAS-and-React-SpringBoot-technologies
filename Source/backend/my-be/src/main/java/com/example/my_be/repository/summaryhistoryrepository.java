package com.example.my_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.model.summaryhistory;
import com.example.my_be.model.summarysession;

@Repository
public interface summaryhistoryrepository extends JpaRepository<summaryhistory, Long> {
    List<summaryhistory> findBySession(summarysession session);
}
