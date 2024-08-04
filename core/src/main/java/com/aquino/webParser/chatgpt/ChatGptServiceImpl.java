/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Category;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
    private static final String KOREAN_TRANSLATION_PROMPT_FORMAT = "Translate the following Japanese text into Korean while maintaining the original spacing and word count. translation only:\n%s";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");
    private static final String CATEGORY_FORMAT = "%s - %s";

    private final SummaryRepository summaryRepository;
    private final String authorization;
    private final ObjectMapper objectMapper;

    private List<Category> categories;
    private List<Category> layer2Categories;

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
    public String getKoreanDescription(Book book) {
        if (book == null || StringUtils.isBlank(book.getDescription())) {
            LOGGER.log(Level.ERROR, "null book or null description");
            return null;
        }

        var isbn = String.valueOf(book.getIsbn());
        var koreanDescription = summaryRepository.getKoreanDescription(isbn);
        if (koreanDescription != null) {
            LOGGER.log(Level.INFO, String.format("Book(%s)'s native summary found in repository.", isbn));
            return koreanDescription;
        }


        var content = getKoreanDescriptionContent(book.getDescription());
        koreanDescription = getChatGptResponse(content);
        if (!StringUtils.isBlank(koreanDescription)) {
            summaryRepository.saveKoreanDescription(isbn, koreanDescription);
        }

        return koreanDescription;
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
            var split = StringUtils.splitByWholeSeparatorPreserveAllTokens(categories, ",");
            book.setCategory(split[0]);
            book.setCategory2(split[1]);
            book.setCategory3(split[2]);

            return book;
        }

        if (this.categories == null || this.categories.size() == 0) {
            LOGGER.log(Level.ERROR, "no categories set");
            return book;
        }

        try {
            setFromChatGpt(book);
        } catch (Exception e) {
            LOGGER.error(String.format("Problem setting category from chatgpt. (%s)", isbn), e);
            return book;
        }

        var combinedCodes = String.join(",", book.getCategory(), book.getCategory2(), book.getCategory3());
        summaryRepository.saveCategory(isbn, combinedCodes);

        return book;
    }

    private Book setFromChatGpt(Book book) {
        var layer2Combined = combineCategories(layer2Categories.stream());
        var category2Code = getCategoryResponse(book, layer2Combined);
        if (category2Code == null) {
            return book;
        }

        var category2 = layer2Categories.stream()
                .filter(c -> c.getCode().equals(category2Code))
                .findFirst()
                .get();
        book.setCategory2(String.format(CATEGORY_FORMAT, category2Code, category2.getName()));

        var firstCategory = this.categories.stream()
                .filter(c -> c.getSubCategories().stream().anyMatch(c2 -> c2.getCode().equals(category2Code)))
                .findFirst()
                .get();
        book.setCategory(String.format(CATEGORY_FORMAT, firstCategory.getCode(), firstCategory.getName()));

        if (category2.getSubCategories() == null || category2.getSubCategories().isEmpty()) {
            LOGGER.log(Level.INFO,
                    String.format("Category (%s) doesn't have any sub categories.", category2.getName()));
            book.setCategory3(StringUtils.EMPTY);

            return book;
        }

        List<Category> layer3Categories = category2
                .getSubCategories()
                .stream()
                .collect(Collectors.toList());
        var layer3Combined = combineCategories(layer3Categories.stream());

        var category3Code = getCategoryResponse(book, layer3Combined);
        var category3 = layer3Categories
                .stream()
                .filter(c -> c.getCode().equals(category3Code))
                .findFirst()
                .get();

        book.setCategory3(String.format(CATEGORY_FORMAT, category3Code, category3.getName()));

        return book;
    }

    private String getCategoryResponse(Book book, String combinedCategories) {
        var content = String.format(CATEGORY_PROMPT_FORMAT, combinedCategories, book.getDescription());
        var response = getChatGptResponse(content);
        var matcher = NUMBER_PATTERN.matcher(response);
        if (!matcher.find()) {
            LOGGER.log(Level.ERROR, String.format("no category code found! [%s]", response));
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
                    .timeout(1_000 * 60 * 2)
                    .execute()
                    .body();
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Problem getting anwser from chatgpt", ex);
            return null;
        }
    }

    private String getRequestBody(String text) throws JsonProcessingException {
        var root = objectMapper.createObjectNode();

        root.put("model", "gpt-4o-mini");

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
        this.layer2Categories = categories
                .stream()
                .flatMap(c -> c.getSubCategories().stream())
                .collect(Collectors.toList());
    }
    
    private String getKoreanDescriptionContent(String description) {
        return String.format(KOREAN_TRANSLATION_PROMPT_FORMAT, description);
    }
}
