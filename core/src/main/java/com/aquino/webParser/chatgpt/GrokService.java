package com.aquino.webParser.chatgpt;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GrokService extends AbstractAiService implements ChatGptService {

    private static final String COMPLETION_URL = "https://api.x.ai/v1/chat/completions";
    private static final String SUMMARY_PROMPT_FORMAT = "_Concise_ summary of book description in English,70 word limit:\n%s";
    private static final String TITLE_PROMPT_FORMAT = "book title, translation only:\n%s";
    private static final String CATEGORY_PROMPT_FORMAT = "classify following text using %s, choose one number only:\n%s";
    private static final String KOREAN_TRANSLATION_PROMPT_FORMAT = "Translate the following Japanese text into Korean while maintaining the original spacing and word count. translation only:\n%s";

    public GrokService(
            ObjectMapper objectMapper,
            String apiKey,
            SummaryRepository summaryRepository) {
        super(objectMapper, apiKey, summaryRepository);
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

        root.put("model", "grok-2-latest");
        root.put("stream", false);
        root.put("temperature", 0.1);

        var messageArray = root.putArray("messages");
        var message = messageArray.addObject();
        message.put("role", "user");
        message.put("content", text);

        return objectMapper.writeValueAsString(root);
    }
}
