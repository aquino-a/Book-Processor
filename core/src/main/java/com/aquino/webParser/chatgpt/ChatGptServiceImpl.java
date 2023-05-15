/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Category;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class ChatGptServiceImpl implements ChatGptService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String COMPLETION_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SUMMARY_PROMPT_FORMAT = "Give a concise summary, less than 100 words, of the book in the following text:\n%s";
    private static final String TITLE_PROMPT_FORMAT = "book title, translation only:\n%s";
    private static final String CATEGORY_PROMPT_FORMAT = "classify following text using %s, choose one number only:\n%s";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\((\\d+)\\)");

    private final SummaryRepository summaryRepository;
    private final String authorization;
    private final ObjectMapper objectMapper;

    private List<Category> categories;

    /**
     * Interfaces with chat GPT.
     *
     * @param objectMapper
     * @param apiKey            API key for open ai.
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

        var content = getSummaryContent(book.getDescription());
        summary = getChatGptResponse(content);
        if (!StringUtils.isBlank(summary)) {
            summaryRepository.save(isbn, summary);
        }

        return summary;
    }

    @Override
    public String getTitle(Book book) {
        if (book == null || StringUtils.isBlank(book.getTitle())) {
            LOGGER.log(Level.ERROR, "null book or null title");
            return null;
        }

        var isbn = String.valueOf(book.getIsbn());
        var title = summaryRepository.getTitle(isbn);
        if (title != null) {
            LOGGER.log(Level.INFO, String.format("Book(%s) found in repository.", isbn));
            return title;
        }

        var content = String.format(TITLE_PROMPT_FORMAT, book.getTitle());
        title = getChatGptResponse(content);
        if (!StringUtils.isBlank(title)) {
            var cleaned = title.replaceAll("\"", "");
            summaryRepository.saveTitle(isbn, cleaned);

            return cleaned;
        }

        return title;
    }

    @Override
    public Book setCategory(Book book) {
        if (book == null || StringUtils.isBlank(book.getDescription())) {
            LOGGER.log(Level.ERROR, "null book or null description");
            return book;
        }

        var isbn = String.valueOf(book.getIsbn());
        var categories = summaryRepository.getCategory(isbn);
        if (categories != null) {
            LOGGER.log(Level.INFO, String.format("Book(%s) found in repository.", isbn));
            var split = StringUtils.split(categories, ',');
            book.setCategory(split[0]);
            book.setCategory2(split[1]);
            book.setCategory3(split[2]);

            return book;
        }

        if (this.categories == null || this.categories.size() == 0) {
            LOGGER.log(Level.ERROR, "no categories set");
            return book;
        }

        setFromChatGpt(book);

        var combinedCodes = String.join(",", book.getCategory(), book.getCategory2(), book.getCategory3());
        summaryRepository.saveCategory(String.valueOf(book.getIsbn()), combinedCodes);

        return book;
    }

    private Book setFromChatGpt(Book book) {
        Stream<Category> secondLayerCategories = this.categories
        .stream()
        .flatMap(c -> c.getSubCategories().stream());
        
        var secondLayerCombined = combineCategories(secondLayerCategories);
        var secondCategoryCode = getCategoryResponse(book, secondLayerCombined);
        if (secondCategoryCode == null) {
            return book;
        }

        book.setCategory2(secondCategoryCode);

        var firstCategory = this.categories.stream()
            .filter(c -> c.getSubCategories().stream().anyMatch(c2 -> c2.getCode().equals(secondCategoryCode)))
            .findFirst()
            .get();
        book.setCategory(firstCategory.getCode());

        Stream<Category> thirdLayerCategories = firstCategory
            .getSubCategories()
            .stream()
            .filter(c -> c.getCode().equals(secondCategoryCode))
            .findFirst()
            .get()
            .getSubCategories()
            .stream();
        var thirdLayerCombined = combineCategories(thirdLayerCategories);

        var thirdCategoryCode = getCategoryResponse(book, thirdLayerCombined);
        book.setCategory3(thirdCategoryCode);

        return book;
    }

    private String getCategoryResponse(Book book, String combinedCategories) {
        var content = String.format(CATEGORY_PROMPT_FORMAT, combinedCategories, book.getDescription());
        var response = getChatGptResponse(content);
        var matcher = NUMBER_PATTERN.matcher(response);
        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1);
    }

    private String combineCategories(Stream<Category> categories) {
        return categories
            .map(c -> String.format("%s (%s)", c.getName(), c.getCode()))
            .reduce((x, y) -> String.format("%s, %s", x, y))
            .get();
    }

    private String getChatGptResponse(String textContent) {
        var responseJson = requestSummary(textContent);
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
        var choices = (ArrayNode) root.path("choices");
        var content = choices.get(0)
                .path("message")
                .path("content")
                .asText();

        return StringUtils.remove(content, '\n');
    }

    private void logUsage(JsonNode root) {
        var totalTokens = root.path("usage").path("total_tokens").asInt();
        LOGGER.log(Level.INFO, String.format("Used %d tokens.", totalTokens));
    }

    private String requestSummary(String content) {
        try {
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

    private String getSummaryContent(String descriptionText) {
        return String.format(SUMMARY_PROMPT_FORMAT, descriptionText);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
