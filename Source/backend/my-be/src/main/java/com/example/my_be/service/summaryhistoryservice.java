package com.example.my_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.example.my_be.dto.request.summaryhistoryrequest;
import com.example.my_be.model.summaryhistory;
import com.example.my_be.model.summarysession;
import com.example.my_be.repository.summaryhistoryrepository;

@Service
public class summaryhistoryservice {
    @Autowired
    private summaryhistoryrepository summaryHistoryRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Inject ObjectMapper

    public summaryhistoryrequest createSummaryHistory(summarysession session, String method, String content) {
        return createSummaryHistory(session, method, content, null);
    }   

    public summaryhistoryrequest createSummaryHistory(summarysession session, String method, String content, Integer grade) {
        summaryhistory history = new summaryhistory();
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
        summaryhistory savedHistory = summaryHistoryRepository.save(history);
        return mapToRequest(savedHistory);
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

    public Optional<summaryhistoryrequest> getSummaryHistoryById(Long historyId) {
        Optional<summaryhistory> historyOpt = summaryHistoryRepository.findById(historyId);
        return historyOpt.map(this::mapToRequest);
    }

    public summaryhistoryrequest updateSummaryHistory(summaryhistoryrequest historyDTO) {
        summaryhistory history = mapToHistory(historyDTO);
        summaryhistory updatedHistory = summaryHistoryRepository.save(history);
        return mapToRequest(updatedHistory);
    }

    public void deleteSummaryHistory(Long historyId) {
        summaryHistoryRepository.deleteById(historyId);
    }

    public List<summaryhistory> findBySession(summarysession session) {
        return summaryHistoryRepository.findBySession(session);
    }

    public summaryhistoryrequest mapToRequest(summaryhistory history) {
        summaryhistoryrequest dto = new summaryhistoryrequest();
        dto.setHistoryId(history.getHistoryId());
        dto.setMethod(history.getMethod());
        dto.setSummaryContent(history.getSummaryContent());
        dto.setIsAccepted(history.getIsAccepted());
        dto.setSessionId(history.getSession().getSessionId());
        dto.setTimestamp(history.getSession().getTimestamp());
        return dto;
    }

    private summaryhistory mapToHistory(summaryhistoryrequest historyDTO) {
        summaryhistory history = new summaryhistory();
        history.setHistoryId(historyDTO.getHistoryId());
        history.setMethod(historyDTO.getMethod());
        history.setSummaryContent(historyDTO.getSummaryContent());
        history.setIsAccepted(historyDTO.getIsAccepted());
        return history;
    }
}
