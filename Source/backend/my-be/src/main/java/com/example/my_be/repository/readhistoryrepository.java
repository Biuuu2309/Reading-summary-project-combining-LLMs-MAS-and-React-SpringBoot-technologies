package com.example.my_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_be.model.readhistory;
import com.example.my_be.model.user;

@Repository
public interface readhistoryrepository extends JpaRepository<readhistory, Long> {
    List<readhistory> findByUser(user user);
}
