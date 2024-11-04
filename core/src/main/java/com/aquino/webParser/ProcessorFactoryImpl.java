package com.aquino.webParser;

import com.aquino.webParser.autofill.*;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.BookCreatorType;
import com.aquino.webParser.bookCreators.aladin.web.AladinBookCreator;
import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.bookCreators.honto.HontoBookCreator;
import com.aquino.webParser.bookCreators.honya.HonyaClubBookCreator;
import com.aquino.webParser.bookCreators.kino.KinoBookCreator;
import com.aquino.webParser.bookCreators.worldcat.WorldCatBookCreator;
import com.aquino.webParser.bookCreators.yahoo.YahooBookCreator;
import com.aquino.webParser.chatgpt.ChatGptService;
import com.aquino.webParser.chatgpt.ChatGptServiceImpl;
import com.aquino.webParser.chatgpt.HibernateSummaryRepository;
import com.aquino.webParser.chatgpt.SummaryRepository;
import com.aquino.webParser.chatgpt.SummaryRepositoryImpl;
import com.aquino.webParser.model.Category;
import com.aquino.webParser.model.Language;
import com.aquino.webParser.model.SavedBook;
import com.aquino.webParser.oclc.OclcService;
import com.aquino.webParser.oclc.OclcServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class ProcessorFactoryImpl {

    /**
     * A map containing the locations of the book properties needed for reading an
     * excel.
     */
    private static final Map<String, Integer> BOOK_PROPERTY_EXCEL_MAP = Stream.of(new Object[][] {
            { "isbn", 0 },
            { "oclc", 3 },
            { "author", 10 },
            { "author2", 13 },
            { "publisher", 16 }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (int) data[1]));
    private static final ObjectMapper OBJECT_MAPPER = createMapper();

    private final HashMap<BookCreatorType, BookCreator> bookCreatorHashMap = new HashMap<>();

    /**
     * A map containing the most common Korean last names and their transliteration.
     */
    private Map<String, String> koreanLastNames;
    private BookWindowService bookWindowService;
    private OclcService oclcService;
    private KinoBookCreator kinoBookCreator;
    private String aladinApiKey;
    private String openaiApiKey;
    private String grokApiKey;
    private List<Category> categories;

    public BookWindowService createWindowService() {
        if (bookWindowService == null)
            bookWindowService = new BookWindowServiceImpl();
        return bookWindowService;
    }

    public BookCreator createBookCreator(BookCreatorType creatorType) throws IOException {
        if (!bookCreatorHashMap.containsKey(creatorType)) {
            BookCreator newCreator;
            switch (creatorType) {
                case AladinApi:
                    newCreator = new AladinBookCreator(createWindowService(), createOclcService(),
                            createChatGptService());
                    break;
                case KinoHontoHonya:
                    var japaneseCreator = createKinoBookCreator();
                    japaneseCreator.setYahoo(new YahooBookCreator());

                    newCreator = japaneseCreator;
                    break;
                case AmazonJapan: {
                    AmazonJapanBookCreator amazonCreator = new AmazonJapanBookCreator(
                            createWindowService(),
                            createOclcService(),
                            createChatGptService());
                    amazonCreator.setHontoBookCreator(new HontoBookCreator());
                    amazonCreator.setHonyaClubBookCreator(new HonyaClubBookCreator());
                    amazonCreator.setYahooBookCreator(new YahooBookCreator());
                    amazonCreator.setWorldCatBookCreator(new WorldCatBookCreator());
                    amazonCreator.setKinoBookCreator(createKinoBookCreator());

                    newCreator = amazonCreator;
                    break;
                }
                default:
                    throw new UnsupportedOperationException(creatorType.toString());
            }

            bookCreatorHashMap.put(creatorType, newCreator);
        }

        return bookCreatorHashMap.get(creatorType);
    }

    private KinoBookCreator createKinoBookCreator() throws IOException {
        if (kinoBookCreator == null) {
            kinoBookCreator = new KinoBookCreator(
                    createWindowService(),
                    createChatGptService(),
                    new HontoBookCreator(),
                    new HonyaClubBookCreator());
        }

        return kinoBookCreator;
    }

    public OclcService createOclcService() {
        if (oclcService == null)
            oclcService = new OclcServiceImpl();
        return oclcService;
    }

    public ChatGptService createChatGptService() throws IOException {
        var chatGptService = new ChatGptServiceImpl(
                OBJECT_MAPPER,
                getOpenAiApiKey(),
                createHibernateSummaryRepository());
        chatGptService.setCategories(categories);

        return chatGptService;
    }

    private SummaryRepository createHibernateSummaryRepository() {
        return new HibernateSummaryRepository(createSessionFactory());
    }

    private SummaryRepository createSummaryRepository() {
        return new SummaryRepositoryImpl("./summary");
    }

    private String getAladinApiKey() throws IOException {
        if (aladinApiKey == null)
            loadProperties();
        return aladinApiKey;
    }

    public String getOpenAiApiKey() throws IOException {
        if (openaiApiKey == null)
            loadProperties();
        return openaiApiKey;
    }

    public String getGrokApiKey() throws IOException {
        if (grokApiKey == null)
            loadProperties();
        return grokApiKey;
    }

    public List<Category> getCategories() throws IOException {
        if (categories == null)
            loadProperties();
        return categories;
    }

    private void loadProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(ProcessorFactoryImpl.class.getClassLoader()
                .getResourceAsStream("config.properties"));
        aladinApiKey = prop.getProperty("aladin.api.key");
        openaiApiKey = prop.getProperty("openai.api.key");
        grokApiKey = prop.getProperty("grok.api.key");
        try (var stream = ProcessorFactoryImpl.class.getClassLoader()
                .getResourceAsStream("categories.json")) {
            categories = OBJECT_MAPPER.readValue(stream, new TypeReference<List<Category>>() {
            });
        }
    }

    public Map<String, Integer> GetExcelMap() {
        return BOOK_PROPERTY_EXCEL_MAP;
    }

    public AutoFillService GetAutoFillService() throws IOException, URISyntaxException {
        var autoFillService = new AutoFillServiceImpl(
                (OclcServiceImpl) this.createOclcService(),
                this.createWindowService(),
                this.GetExcelMap(), getAuthorStrategies());
        return autoFillService;
    }

    private Map<Language, AuthorStrategy> getAuthorStrategies() throws IOException, URISyntaxException {
        return Map.of(
                Language.Korean, new KoreanAuthorStrategy(GetKoreanLastNames()),
                Language.Japanese, new JapaneseAuthorStrategy());
    }

    public Map<String, String> GetKoreanLastNames() throws URISyntaxException, IOException {
        if (koreanLastNames == null) {
            try (
                    var lastNamesStream = this.getClass().getClassLoader()
                            .getResourceAsStream("korean-last-names.csv");
                    var br = new BufferedReader(new InputStreamReader(lastNamesStream, StandardCharsets.UTF_8))) {
                koreanLastNames = br.lines()
                        .map(name -> name.split(","))
                        .filter(array -> array.length == 2)
                        .collect(Collectors.toMap(array -> array[0].strip(), array -> array[1].strip()));
            }
        }
        return koreanLastNames;
    }

    private static ObjectMapper createMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        return mapper;
    }

    private static SessionFactory createSessionFactory() {
        final var registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();

        try {
            var metadataSources = new MetadataSources(registry);
            metadataSources.addAnnotatedClass(SavedBook.class);

            return metadataSources
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder
                    .destroy(registry);
            throw e;
        }
    }
}
