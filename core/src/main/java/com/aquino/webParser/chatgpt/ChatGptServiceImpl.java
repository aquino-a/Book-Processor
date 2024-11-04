/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;


public class ChatGptServiceImpl extends AbstractAiService implements ChatGptService {

    private static final String COMPLETION_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SUMMARY_PROMPT_FORMAT = "Give a concise summary, less than 100 words, of the book in the following text:\n%s";
    private static final String TITLE_PROMPT_FORMAT = "book title, translation only:\n%s";
    private static final String CATEGORY_PROMPT_FORMAT = "classify following text using %s, choose one number only:\n%s";
    private static final String KOREAN_TRANSLATION_PROMPT_FORMAT = "Translate the following Japanese text into Korean while maintaining the original spacing and word count. translation only:\n%s";

    public ChatGptServiceImpl(
            ObjectMapper objectMapper,
            String apiKey,
            SummaryRepository summaryRepository) {
        super(objectMapper, apiKey, summaryRepository);
    }

    @Override
    protected String getResponseContent(JsonNode root) {
        var choices = (ArrayNode) root.path("choices");
        var content = choices.get(0)
                .path("message")
                .path("content")
                .asText();

        return StringUtils.remove(content, '\n');
    }

    @Override
    protected String getRequestBody(String text) throws JsonProcessingException {
        var root = objectMapper.createObjectNode();

        root.put("model", "gpt-3.5-turbo");

        var messageArray = root.putArray("messages");
        var message = messageArray.addObject();
        message.put("role", "user");
        message.put("content", text);

        return objectMapper.writeValueAsString(root);
    }

    @Override
    protected String completionUrl() {
        return COMPLETION_URL;
    }

    @Override
    protected String summaryPrompt() {
        return SUMMARY_PROMPT_FORMAT;
    }

    @Override
    protected String titlePrompt() {
        return TITLE_PROMPT_FORMAT;
    }

    @Override
    protected String categoryPrompt() {
        return CATEGORY_PROMPT_FORMAT;
    }

    @Override
    protected String koreanTranslationPrompt() {
        return KOREAN_TRANSLATION_PROMPT_FORMAT;
    }

}
