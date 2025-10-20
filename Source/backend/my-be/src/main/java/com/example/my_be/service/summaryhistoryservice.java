package com.example.demo.service;

import com.example.demo.model.SummaryHistory;
import com.example.demo.model.SummarySession;
import com.example.demo.repository.SummaryHistoryRepository;
import com.example.demo.dto.SummaryHistoryDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SummaryHistoryService {

    @Autowired
    private SummaryHistoryRepository summaryHistoryRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Inject ObjectMapper

    public SummaryHistoryDTO createSummaryHistory(SummarySession session, String method, String content) {
        return createSummaryHistory(session, method, content, null);
    }

    public SummaryHistoryDTO createSummaryHistory(SummarySession session, String method, String content, Integer grade) {
        SummaryHistory history = new SummaryHistory();
        history.setSession(session);
        history.setMethod(method);

        if (method.equals("paraphrase")) {
            String summarizedContent = callParaphraseApi(content, grade);
            history.setSummaryContent(summarizedContent != null ? summarizedContent : "Không thể diễn giải nội dung");
        } else if (method.equals("extraction") || method.equals("extract")) {
            String summarizedContent = callExtractionApi(content, grade);
            history.setSummaryContent(summarizedContent != null ? summarizedContent : "Không thể trích xuất nội dung");
        } else {
            history.setSummaryContent("Phương thức không được hỗ trợ: " + method);
        }

        history.setIsAccepted(false);

        System.out.println("Saving summaryContent: " + history.getSummaryContent());
        SummaryHistory savedHistory = summaryHistoryRepository.save(history);
        return mapToDTO(savedHistory);
    }

    private String callParaphraseApi(String text, Integer grade) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
    
            String url = "http://localhost:5001/summarize";
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("text", text);
            if (grade != null) {
                payload.put("grade", grade);
            }
            payload.put("max_tokens", 512);
            String json = objectMapper.writeValueAsString(payload);
            System.out.println("Paraphrase API payload: " + json);
    
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
    
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("Paraphrase API raw response: " + response.getBody());
    
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode summaryNode = jsonNode.get("summary");
                if (summaryNode != null && !summaryNode.isNull()) {
                    String summaryText = summaryNode.asText();
                    System.out.println("Paraphrase API response: " + summaryText);
                    return summaryText;
                }
                System.err.println("Paraphrase API returned no summary field: " + response.getBody());
                return "Không thể diễn giải nội dung (API không trả về summary)";
            } else {
                System.err.println("Failed to paraphrase text, API returned: " + response.getStatusCode());
                return "Không thể diễn giải nội dung (API lỗi: " + response.getStatusCode() + ")";
            }
        } catch (Exception e) {
            System.err.println("Error calling paraphrase API: " + e.getMessage());
            e.printStackTrace();
            return "Không thể diễn giải nội dung (Lỗi: " + e.getMessage() + ")";
        }
    }
    private String callExtractionApi(String text, Integer grade) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = "http://localhost:8000/summarize";
            // Build JSON payload using ObjectMapper
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("text", text);
            if (grade != null) {
                payload.put("grade", grade);
            }
            String json = objectMapper.writeValueAsString(payload);
            System.out.println("Extraction API payload: " + json);

            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode summaryNode = jsonNode.get("summary");
                if (summaryNode != null && !summaryNode.isNull()) {
                    System.out.println("Extraction API response: " + summaryNode.asText());
                    return summaryNode.asText();
                } else {
                    System.err.println("Extraction API returned no summary field: " + response.getBody());
                    return "Không thể trích xuất nội dung (API không trả về summary)";
                }
            } else {
                System.err.println("Failed to extract text, API returned: " + response.getStatusCode());
                return "Không thể trích xuất nội dung (API lỗi: " + response.getStatusCode() + ")";
            }
        } catch (Exception e) {
            System.err.println("Error calling extraction API: " + e.getMessage());
            e.printStackTrace();
            return "Không thể trích xuất nội dung (Lỗi: " + e.getMessage() + ")";
        }
    }

    public Optional<SummaryHistoryDTO> getSummaryHistoryById(Long historyId) {
        Optional<SummaryHistory> historyOpt = summaryHistoryRepository.findById(historyId);
        return historyOpt.map(this::mapToDTO);
    }

    public SummaryHistoryDTO updateSummaryHistory(SummaryHistoryDTO historyDTO) {
        SummaryHistory history = mapToEntity(historyDTO);
        SummaryHistory updatedHistory = summaryHistoryRepository.save(history);
        return mapToDTO(updatedHistory);
    }

    public void deleteSummaryHistory(Long historyId) {
        summaryHistoryRepository.deleteById(historyId);
    }

    public List<SummaryHistory> findBySession(SummarySession session) {
        return summaryHistoryRepository.findBySession(session);
    }

    public SummaryHistoryDTO mapToDTO(SummaryHistory history) {
        SummaryHistoryDTO dto = new SummaryHistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setMethod(history.getMethod());
        dto.setSummaryContent(history.getSummaryContent());
        dto.setIsAccepted(history.getIsAccepted());
        dto.setSessionId(history.getSession().getSessionId());
        dto.setTimestamp(history.getSession().getTimestamp());
        return dto;
    }

    private SummaryHistory mapToEntity(SummaryHistoryDTO historyDTO) {
        SummaryHistory history = new SummaryHistory();
        history.setHistoryId(historyDTO.getHistoryId());
        history.setMethod(historyDTO.getMethod());
        history.setSummaryContent(historyDTO.getSummaryContent());
        history.setIsAccepted(historyDTO.getIsAccepted());
        return history;
    }
}