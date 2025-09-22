package com.example.my_be.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_be.model.readhistory;
import com.example.my_be.model.summary;
import com.example.my_be.model.user;
import com.example.my_be.repository.readhistoryrepository;

@Service
public class readhistoryservice {
    @Autowired
    private readhistoryrepository readHistoryRepository;

    public readhistory logReadHistory(user user, summary summary) {
        readhistory readHistory = new readhistory();
        readHistory.setUser(user);
        readHistory.setSummary(summary);

        return readHistoryRepository.save(readHistory);
    }

    public List<readhistory> getReadHistoryByUser(user user) {
        List<readhistory> histories = readHistoryRepository.findByUser(user);
        
        for (readhistory history : histories) {
            history.getUser().getUsername();
            history.getSummary().getTitle();
        }
        return histories;
    }
}
