package com.aquino.webParser.bookCreators.honto;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// publish date, age group, size
public class HontoBookCreator implements BookCreator {

    // dyTitle
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String HONTO_URL = "https://honto.jp";
    private static final String SEARCH_URL_FORMAT = "https://honto.jp/netstore/search_10%s.html?srchf=1&tbty=0";
    private static final DateTimeFormatter DATE_SOURCE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DATE_TARGET_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d+/\\d+/\\d+");
    private static final Pattern SIZE_PATTERN = Pattern.compile("：([\\d０-９]+) ?(?:×|x) ?([\\d０-９]+)(?:ｃｍ|cm)");
    private static final Predicate<String> AGE_GROUP_PREDICATE = Pattern.compile("(カテゴリ|利用対象)").asPredicate();
    private static final Predicate<String> PUBLISHER_PREDICATE = Pattern.compile("(出版社)").asPredicate();
    private static final String SIZE_FORMAT = "%s x %s";
    private Map<String, String> cookies;

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if (doc == null)
            throw new IOException(String.format("Search Document wasn't loaded: %s", isbn));

        try {
            return createBookFromBookPage(
                    doc.getElementsByClass("dyTitle")
                            .first().attr("href"));
        } catch (Exception e) {
            throw new IOException(String.format("Book page not found: %s", isbn));
        }
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        Book book = new Book();
        book.setBookPageUrl(bookPageUrl);

        var doc = getDoc(bookPageUrl);

        fillInBasicData(book, doc);
        return book;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        return null;
    }

    @Override
    public String BookPagePrefix() {
        return null;
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        return null;
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        return null;
    }

    @Override
    public void checkInventoryAndOclc(Book result) {
    }

    // publish date, age group, size
    private void fillInBasicData(Book book, Document doc) {
        book.setPublisher(findPublisher(doc));
        book.setPublishDateFormatted(getFormattedPublishDate(doc));
        book.setAgeGroup(findAgeGroup(doc));
        book.setBookSizeFormatted(getFormattedBookSize(doc));
    }

    private String findPublisher(Document doc) {
        try {
            var optionalAgeGroup = doc.getElementsByClass("stItemData")
                    .first()
                    .getElementsByTag("li")
                    .stream()
                    .filter(e -> PUBLISHER_PREDICATE.test(e.text()))
                    .map(e -> e.text())
                    .map(s -> StringUtils.substringAfter(s, "："))
                    .map(String::strip)
                    .findFirst();
                    
            if (optionalAgeGroup.isEmpty()) {
                throw new RuntimeException("no publisher found in item data.");
            } else {
                return optionalAgeGroup.get();
            }
        } catch (Exception e) {
            LOGGER.error("Couldn't find publisher.", e);
            return null;
        }
    }

    // <ul class="stItemData">
    // <li>カテゴリ：小学生</li>
    // <li>発売日：2023/06/19</li>
    // <li>出版社：
    // <a href="https://honto.jp/netstore/search/pb_9000034309.html">岩波書店</a>
    // </li>
    // <li>レーベル：
    // <a href="https://honto.jp/netstore/search/lb_9000000018.html">岩波少年文庫</a>
    // </li>
    // <li>サイズ：１８ｃｍ／３１０，６ｐ</li>
    // <li>利用対象：小学生</li>
    // <li>ISBN：978-4-00-114258-7</li>
    // <!-- ul.stItemData--></ul>
    private String getFormattedPublishDate(Document doc) {
        try {
            var itemDataText = doc.getElementsByClass("stItemData")
                    .first()
                    .text();

            var matcher = DATE_PATTERN.matcher(itemDataText);
            if (matcher.find()) {
                var unformattedDate = matcher.group(0);
                var date = LocalDate.parse(unformattedDate, DATE_SOURCE_FORMATTER);

                return date.format(DATE_TARGET_FORMATTER);
            } else {
                throw new RuntimeException("Date regex didn't match.");
            }
        } catch (Exception e) {
            LOGGER.error("Couldn't get publish date.", e);
            return null;
        }
    }

    private String findAgeGroup(Document doc) {
        try {
            var optionalAgeGroup = doc.getElementsByClass("stItemData")
                    .first()
                    .getElementsByTag("li")
                    .stream()
                    .filter(e -> AGE_GROUP_PREDICATE.test(e.ownText()))
                    .map(e -> e.ownText())
                    .map(s -> StringUtils.substringAfter(s, "："))
                    .findFirst();
                    
            if (optionalAgeGroup.isEmpty()) {
                throw new RuntimeException("no age group found in item data.");
            } else {
                return optionalAgeGroup.get();
            }
        } catch (Exception e) {
            LOGGER.info("Couldn't find age group.", e);
            return null;
        }
    }

    private String getFormattedBookSize(Document doc) {
        try {
            var itemDataText = doc.getElementsByClass("stItemData")
                    .first()
                    .text();

            var matcher = SIZE_PATTERN.matcher(itemDataText);
            if (matcher.find()) {
                var firstNum = toHalfWidth(matcher.group(1));
                var secondNum = toHalfWidth(matcher.group(2));

                return String.format(SIZE_FORMAT, firstNum, secondNum);
            } else {
                throw new RuntimeException("Size regex didn't match.");
            }
        } catch (Exception e) {
            LOGGER.info("Couldn't find book size.", e);
            return null;
        }
    }

    // TODO extract to base class
    private Document getDoc(String bookPageUrl) throws IOException {
        if (cookies == null) {
            cookies = getCookies();
        }

        return Jsoup.connect(bookPageUrl)
                .cookies(cookies)
                .get();
    }

    // TODO extract to base class
    private Map<String, String> getCookies() throws IOException {

        var response = Jsoup.connect(HONTO_URL)
                .execute();

        return response.cookies();
    }

    public static String toHalfWidth(String input) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // Check if the character is a full-width digit
            if (ch >= 65296 && ch <= 65305) {
                // Convert the full-width digit to half-width by subtracting the difference
                char halfWidthDigit = (char) (ch - (65296 - 48));
                result.append(halfWidthDigit);
            } else {
                // Character is not a full-width digit, keep it as it is
                result.append(ch);
            }
        }

        return result.toString();
    }
}
