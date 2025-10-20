// CreateSummarySessionRequest.java
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSummarySessionRequest {
    private String userId;
    private String content;
}
