package com.aquino.webParser.chatgpt;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aquino.webParser.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GrokService implements ChatGptService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String COMPLETION_URL = "https://api.x.ai/v1/chat/completions";
    private static final String SUMMARY_PROMPT_FORMAT = "Concise summary of book description in english,100 word limit:\n%s";

    private final ChatGptServiceImpl chatGptServiceImpl;
    private final SummaryRepository summaryRepository;
    private final String authorization;
    private final ObjectMapper objectMapper;
    
    public GrokService(
            ChatGptServiceImpl chatGptServiceImpl, 
            SummaryRepository summaryRepository, 
            String authorization,
            ObjectMapper objectMapper) {
        this.chatGptServiceImpl = chatGptServiceImpl;
        this.summaryRepository = summaryRepository;
        this.authorization = authorization;
        this.objectMapper = objectMapper;
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

        var content = getSummaryContent(book.getDescription());
        summary = getChatGptResponse(content);
        if (!StringUtils.isBlank(summary)) {
            summaryRepository.save(isbn, summary);
        }

        return summary;
    }

    @Override
    public String getKoreanDescription(Book book) {
        return chatGptServiceImpl.getKoreanDescription(book);
    }

    @Override
    public String getTitle(Book book) {
        return chatGptServiceImpl.getTitle(book);
    }

    @Override
    public Book setCategory(Book book) {
        return chatGptServiceImpl.setCategory(book);
    }
}
