package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "summary_history")
@Data
@NoArgsConstructor
public class SummaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId; // Unique ID for the history entry

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private SummarySession session; // Link to a specific session

    @Column(nullable = false)
    private String method; // The summarization method used (e.g., "PHOBERT" or "T5_DIEN_GIAI")

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    @Lob
    private String summaryContent; // The generated summary content


    @Column(nullable = false)
    private Boolean isAccepted; 
    // Other relevant fields such as date of creation or modification can be added
}
