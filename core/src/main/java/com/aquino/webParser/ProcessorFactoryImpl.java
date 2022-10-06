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
import com.aquino.webParser.model.Language;
import com.aquino.webParser.oclc.OclcService;
import com.aquino.webParser.oclc.OclcServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessorFactoryImpl {

    /**
     * A map containing the locations of the book properties needed for reading an excel.
     */
    private static final Map<String, Integer> BOOK_PROPERTY_EXCEL_MAP = Stream.of(new Object[][]{
        {"isbn", 0},
        {"oclc", 3},
        {"author", 10},
        {"author2", 13},
        {"publisher", 16}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (int) data[1]));

    private final HashMap<BookCreatorType, BookCreator> bookCreatorHashMap = new HashMap<>();

    /**
     * A map containing the most common Korean last names and their transliteration.
     */
    private Map<String, String> koreanLastNames;
    private BookWindowService bookWindowService;
    private OclcService oclcService;
    private String aladinApiKey;


    public BookWindowService CreateWindowService() {
        if (bookWindowService == null)
            bookWindowService = new BookWindowServiceImpl();
        return bookWindowService;
    }

    public BookCreator CreateBookCreator(BookCreatorType creatorType) throws IOException {
        if (!bookCreatorHashMap.containsKey(creatorType)) {
            BookCreator newCreator;
            switch (creatorType) {
                case AladinApi:
                    newCreator = new AladinBookCreator(CreateWindowService(), CreateOclcService());
                    break;
                case AmazonJapan: {
                    AmazonJapanBookCreator abc = new AmazonJapanBookCreator(CreateWindowService(), CreateOclcService());
                    abc.setHontoBookCreator(new HontoBookCreator());
                    abc.setHonyaClubBookCreator(new HonyaClubBookCreator());
                    abc.setYahooBookCreator(new YahooBookCreator());
                    abc.setWorldCatBookCreator(new WorldCatBookCreator());
                    abc.setKinoBookCreator(new KinoBookCreator());
                    newCreator = abc;

                }
                break;
                default:
                    throw new UnsupportedOperationException(creatorType.toString());
            }
            bookCreatorHashMap.put(creatorType, newCreator);
        }
        return bookCreatorHashMap.get(creatorType);
    }

    public OclcService CreateOclcService() {
        if (oclcService == null)
            oclcService = new OclcServiceImpl();
        return oclcService;
    }

    private String getAladinApiKey() throws IOException {
        if (aladinApiKey == null)
            loadProperties();
        return aladinApiKey;
    }

    private void loadProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(ProcessorFactoryImpl.class.getClassLoader()
            .getResourceAsStream("config.properties"));
        aladinApiKey = prop.getProperty("aladin.api.key");
    }

    public Map<String, Integer> GetExcelMap() {
        return BOOK_PROPERTY_EXCEL_MAP;
    }

    public AutoFillService GetAutoFillService() throws IOException, URISyntaxException {
        var autoFillService = new AutoFillServiceImpl(
            new WorldCatBookCreator(),
            this.CreateWindowService(),
            this.GetExcelMap(), getAuthorStrategies());
        return autoFillService;
    }

    private Map<Language, AuthorStrategy> getAuthorStrategies() throws IOException, URISyntaxException {
        return Map.of(
            Language.Korean, new KoreanAuthorStrategy(GetKoreanLastNames()),
            Language.Japanese, new JapaneseAuthorStrategy()
        );
    }

    public Map<String, String> GetKoreanLastNames() throws URISyntaxException, IOException {
        if (koreanLastNames == null) {
            try (
                var lastNamesStream = this.getClass().getClassLoader()
                    .getResourceAsStream("korean-last-names.csv");
                var br = new BufferedReader(new InputStreamReader(lastNamesStream, StandardCharsets.UTF_8))
            ) {
                koreanLastNames = br.lines()
                    .map(name -> name.split(","))
                    .filter(array -> array.length == 2)
                    .collect(Collectors.toMap(array -> array[0].strip(), array -> array[1].strip()));
            }
        }
        return koreanLastNames;
    }
}
