package com.aquino.webParser.chatgpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GrokService extends AbstractAiService implements ChatGptService {

    private static final String COMPLETION_URL = "https://api.x.ai/v1/chat/completions";
    private static final String SUMMARY_PROMPT_FORMAT = "Concise summary of book description in english,100 word limit:\n%s";
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResponseContent'");
    }

    @Override
    protected String getRequestBody(String text) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequestBody'");
    }
}
