/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class ChatGptServiceImpl implements ChatGptService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String COMPLETION_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SUMMARY_PROMPT_FORMAT = "Give a summary of the contents of the book mentioned in the following text in less than 100 words:\n%s";

    private final String authorization;
    private final ObjectMapper objectMapper;

    /**
     * Interfaces with chat GPT.
     * 
     * @param objectMapper
     * @param apiKey API key for open ai.
     */
    public ChatGptServiceImpl(ObjectMapper objectMapper, String apiKey) {
        this.objectMapper = objectMapper;
        this.authorization = "Bearer " + apiKey;
    }

    @Override
    public String getSummary(String descriptionText) {
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
