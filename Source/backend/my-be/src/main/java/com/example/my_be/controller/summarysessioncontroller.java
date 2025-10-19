package com.example.my_be.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.my_be.model.summarysession;
import com.example.my_be.service.summarysessionservice;
import com.example.my_be.service.summarysessionservice.ImageUploadResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/summarysession")
public class summarysessioncontroller {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private summarysessionservice summarySessionService;

    // Inject the Gemini API key from application.properties
    private String geminiApiKey = System.getenv("GEMINI_API_KEY");

    // Define the base Gemini API URL
    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    // Create a new summary session
    @PostMapping
    public ResponseEntity<summarysession> createSummarySession(@RequestBody summarysession session) {
        summarysession createdSession = summarySessionService.createSummarySession(session);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    // Get a summary session by ID
    @GetMapping("/{sessionId}")
    public ResponseEntity<summarysession> getSummarySessionById(@PathVariable Long sessionId) {
        Optional<summarysession> sessionOpt = summarySessionService.getSummarySessionById(sessionId);
        return sessionOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/process-pdf")
    public ResponseEntity<String> processPdf(@RequestParam("file") MultipartFile file) {
        ObjectMapper mapper = new ObjectMapper();
    
        try {
            System.out.println("Received file: " + file.getOriginalFilename() + ", Size: " + file.getSize());
    
            // Step 1: Call Flask service to get cleaned text
            HttpHeaders flaskHeaders = new HttpHeaders();
            flaskHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
    
            HttpEntity<MultiValueMap<String, Object>> flaskEntity = new HttpEntity<>(params, flaskHeaders);
            System.out.println("Sending request to Flask: " + params.toString());
    
            ResponseEntity<String> flaskResponse = restTemplate.postForEntity(
                "http://127.0.0.1:8080/process-pdf",
                flaskEntity,
                String.class
            );
    
            System.out.println("Flask response status: " + flaskResponse.getStatusCode());
            String flaskResponseBody = flaskResponse.getBody();
            System.out.println("Raw Flask response: " + flaskResponseBody);
    
            if (!flaskResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to process PDF, Flask returned: " + flaskResponse.getStatusCode());
            }
    
            JsonNode flaskJsonNode = mapper.readTree(flaskResponseBody);
            String cleanedText = flaskJsonNode.get("cleaned_text").asText();
            System.out.println("Extracted cleaned text: " + cleanedText);
    
            // Step 2: Call Gemini API to generate titles
            String geminiApiUrl = GEMINI_API_BASE_URL + geminiApiKey;
    
            String prompt = "Bạn là một nhà văn chuyên viết truyện ngắn cho thiếu nhi. Dựa trên câu chuyện sau, hãy tạo 4-5 tiêu đề ngắn gọn, thú vị, phù hợp với nội dung và trả về dưới dạng JSON chính xác như sau: {\"titles\": [\"Tiêu đề 1\", \"Tiêu đề 2\", \"Tiêu đề 3\", \"Tiêu đề 4\", \"Tiêu đề 5\"]}. **CHỈ TRẢ VỀ JSON, KHÔNG THÊM BẤT KỲ KÝ TỰ NÀO KHÁC NHƯ ```json HOẶC Markdown**. Đây là nội dung câu chuyện:\n" +
                           "Người chăn cừu nuôi cả nghìn con cừu trong nông trường ông ta và các con đều sống ở đó. Người chăn cừu có hai người hàng xóm Chim Ưng..." + cleanedText;
    
            System.out.println("Gemini prompt (raw): " + prompt);
    
            ObjectNode requestBody = mapper.createObjectNode();
            ArrayNode contentsArray = mapper.createArrayNode();
            ObjectNode contentNode = mapper.createObjectNode();
            ArrayNode partsArray = mapper.createArrayNode();
            ObjectNode partNode = mapper.createObjectNode();
    
            partNode.put("text", prompt);
            partsArray.add(partNode);
            contentNode.set("parts", partsArray);
            contentsArray.add(contentNode);
            requestBody.set("contents", contentsArray);
    
            String geminiRequestBody = mapper.writeValueAsString(requestBody);
            System.out.println("Gemini request body: " + geminiRequestBody);
    
            HttpHeaders geminiHeaders = new HttpHeaders();
            geminiHeaders.setContentType(MediaType.APPLICATION_JSON);
    
            HttpEntity<String> geminiEntity = new HttpEntity<>(geminiRequestBody, geminiHeaders);
    
            ResponseEntity<String> geminiResponse = restTemplate.postForEntity(
                geminiApiUrl,
                geminiEntity,
                String.class
            );
    
            System.out.println("Gemini response status: " + geminiResponse.getStatusCode());
            String geminiResponseBody = geminiResponse.getBody();
            System.out.println("Raw Gemini response: " + geminiResponseBody);
    
            if (!geminiResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to generate titles, Gemini returned: " + geminiResponse.getStatusCode() + " - " + geminiResponseBody);
            }
    
            // Step 3: Extract and process the Gemini response
            JsonNode geminiJsonNode = mapper.readTree(geminiResponseBody);
            String geminiText = geminiJsonNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            System.out.println("Gemini generated text: " + geminiText);
    
            // Step 4: Remove Markdown formatting
            String cleanedGeminiText = geminiText.replace("```json", "").replace("```", "").trim();
            System.out.println("Cleaned Gemini text: " + cleanedGeminiText);
    
            // Step 5: Parse the cleaned response with validation
            JsonNode titlesJson;
            try {
                titlesJson = mapper.readTree(cleanedGeminiText);
            } catch (IOException e) {
                System.out.println("Failed to parse Gemini response as JSON: " + e.getMessage());
                System.out.println("Using fallback titles due to parsing error.");
                titlesJson = mapper.readTree("{\"titles\": [\"Tiêu đề dự phòng 1\", \"Tiêu đề dự phòng 2\", \"Tiêu đề dự phòng 3\", \"Tiêu đề dự phòng 4\", \"Tiêu đề dự phòng 5\"]}");
            }
    
            JsonNode titlesArray = titlesJson.get("titles");
            if (titlesArray == null || !titlesArray.isArray() || titlesArray.size() < 4) {
                System.out.println("Titles array is missing, invalid, or too short. Using fallback.");
                titlesArray = mapper.readTree("[\"Tiêu đề dự phòng 1\", \"Tiêu đề dự phòng 2\", \"Tiêu đề dự phòng 3\", \"Tiêu đề dự phòng 4\", \"Tiêu đề dự phòng 5\"]");
            }
            System.out.println("Parsed titles array: " + titlesArray.toString());
    
            // Step 6: Combine cleaned text and titles into response
            ObjectNode responseJson = mapper.createObjectNode();
            responseJson.put("cleanedText", cleanedText);
            responseJson.set("titles", titlesArray);
    
            String jsonResponse = mapper.writeValueAsString(responseJson);
            System.out.println("Final response to frontend: " + jsonResponse);
    
            return ResponseEntity.ok(jsonResponse);
    
        } catch (IOException e) {
            System.err.println("Error processing PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\": \"Error processing PDF: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\": \"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
    
    @PutMapping("/{sessionId}")
    public ResponseEntity<summarysession> updateSummarySession(@PathVariable Long sessionId, @RequestBody summarysession updatedSession) {
        Optional<summarysession> sessionOpt = summarySessionService.getSummarySessionById(sessionId);
        if (sessionOpt.isPresent()) {
            summarysession existingSession = sessionOpt.get();
            existingSession.setContent(updatedSession.getContent());
            summarysession updatedSessionEntity = summarySessionService.updateSummarySession(existingSession);
            return ResponseEntity.ok(updatedSessionEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate-image")
    public ResponseEntity<ImageUploadResult> generateImageAndUploadToCloudinary(@RequestBody summarysession session) {
        String content = session.getContent();
        ImageUploadResult result = summarySessionService.generateImageAndUploadToCloudinary(content);
        return ResponseEntity.ok(result);
    }

    // New endpoint to upload an image to Cloudinary
    @PostMapping("/upload-image")
    public ResponseEntity<ImageUploadResult> uploadImageToCloudinary(@RequestParam("file") MultipartFile file) {
        System.out.println("Received upload request for file: " + file.getOriginalFilename() + ", Size: " + file.getSize());
        try {
            ImageUploadResult result = summarySessionService.uploadImageToCloudinary(file);
            System.out.println("Upload successful, URL: " + result.getImageUrl());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(summarySessionService.new ImageUploadResult(null, false));
        }
    }

    // Delete a summary session by ID
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSummarySession(@PathVariable Long sessionId) {
        summarySessionService.deleteSummarySession(sessionId.toString());
        return ResponseEntity.noContent().build();
    }

    // Inner class for processed PDF response with cleaned text and titles
    private static class ProcessedPdfResponse {
        private String cleanedText;
        private JsonNode titles;

        public ProcessedPdfResponse(String cleanedText, JsonNode titles) {
            this.cleanedText = cleanedText;
            this.titles = titles;
        }

        public String getCleanedText() {
            return cleanedText;
        }

        public void setCleanedText(String cleanedText) {
            this.cleanedText = cleanedText;
        }

        public JsonNode getTitles() {
            return titles;
        }

        public void setTitles(JsonNode titles) {
            this.titles = titles;
        }
    }
}
