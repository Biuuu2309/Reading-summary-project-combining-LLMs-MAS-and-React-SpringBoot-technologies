package com.example.my_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.entity.summary;

@Repository
public interface summaryrepository extends JpaRepository<summary, String> {

}
