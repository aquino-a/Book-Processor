/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import com.aquino.webParser.model.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class ChatGptServiceImpl implements ChatGptService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String COMPLETION_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SUMMARY_PROMPT_FORMAT = "Give a summary of the contents of the book mentioned in the following text in less than 100 words:\n%s";

    private final SummaryRepository summaryRepository;
    private final String authorization;
    private final ObjectMapper objectMapper;

    /**
     * Interfaces with chat GPT.
     *
     * @param objectMapper
     * @param apiKey API key for open ai.
     * @param summaryRepository
     */
    public ChatGptServiceImpl(
            ObjectMapper objectMapper,
            String apiKey,
            SummaryRepository summaryRepository) {
        this.objectMapper = objectMapper;
        this.authorization = "Bearer " + apiKey;
        this.summaryRepository = summaryRepository;
    }

    @Override
    public String getSummary(Book book) {
        if (book == null || StringUtils.isBlank(book.getDescription())) {
            LOGGER.log(Level.ERROR, "null book or null description");
            return null;
        }

        var isbn = String.valueOf(book.getIsbn());
        var summary = summaryRepository.get(isbn);
        if (summary != null) {
            LOGGER.log(Level.INFO, String.format("Book(%s) found in repository.", isbn));
            return summary;
        }

        summary = getChatGptSummary(book.getDescription());
        if (!StringUtils.isBlank(summary)) {
            summaryRepository.save(isbn, summary);
        }

        return summary;
    }

    private String getChatGptSummary(String descriptionText) {
        var responseJson = requestSummary(descriptionText);
        if (responseJson == null || StringUtils.isBlank(responseJson)) {
            return null;
        }

        try {
            var root = objectMapper.readTree(responseJson);
            String content = getResponseContent(root);
            logUsage(root);

            return content;
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.ERROR, "Problem reading chatgpt response.", e);
            return null;
        }
    }

    private String getResponseContent(JsonNode root) {
        var content = root.path("choices")
                .path("message")
                .asText("content");
        
        return StringUtils.remove(content, '\n');
    }

    private void logUsage(JsonNode root) {
        var totalTokens = root.path("usage").path("total_tokens").asInt();
        LOGGER.log(Level.INFO, String.format("Used %d tokens.", totalTokens));
    }

    private String requestSummary(String descriptionText) {
        try {
            var content = getFullContent(descriptionText);
            var body = getRequestBody(content);

            return Jsoup.connect(COMPLETION_URL)
                    .header("Content-Type", "application/json")
                    .header("Authorization", authorization)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .requestBody(body)
                    .execute()
                    .body();
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Problem getting anwser from chatgpt", ex);
            return null;
        }
    }

    private String getRequestBody(String text) throws JsonProcessingException {
        var root = objectMapper.createObjectNode();

        root.put("model", "gpt-3.5-turbo");

        var messageArray = root.putArray("messages");
        var message = messageArray.addObject();
        message.put("role", "user");
        message.put("content", text);

        return objectMapper.writeValueAsString(root);
    }

    private String getFullContent(String descriptionText) {
        return String.format(SUMMARY_PROMPT_FORMAT, descriptionText);
    }
}
