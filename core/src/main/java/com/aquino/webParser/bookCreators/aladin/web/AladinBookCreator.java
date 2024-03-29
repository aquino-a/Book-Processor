package com.aquino.webParser.bookCreators.aladin.web;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.chatgpt.ChatGptService;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.oclc.OclcService;
import com.aquino.webParser.romanization.Romanizer;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;


import java.util.regex.Pattern;

public class AladinBookCreator implements BookCreator {

    private static final Pattern TRANSLATOR_PATTERN =
        Pattern.compile("(?:([\\u3131-\\uD79D ]+),? *)+\\(옮긴이\\)");
    private static final Pattern ORIGINAL_TITLE_PATTERN =
        Pattern.compile("원제 : ([\\p{L}:'\\*,0-9’\\- ]+)(?: \\( \\d{4}년\\))?");
    private static final Pattern PUBLISHER_PATTERN =
        Pattern.compile("\\)([\\u3131-\\uD79DA-Za-z ]+)(?:\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern PUBLISH_DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern AUTHORS_PATTERN =
        Pattern.compile("(?:([\\u3131-\\uD79D ]+),? *)+\\(지은이\\)");
    private static final Pattern NAMES_PATTERN =
        Pattern.compile("([\\u3131-\\uD79D ]+)(?:,| )");

    private static final Pattern OCLC_PATTERN = Pattern.compile("oclc/(\\d+)$");
    private static final Logger LOGGER = LogManager.getLogger();
    private final BookWindowService bookWindowService;
    private final OclcService oclcService;
    private final ChatGptService chatGptService;


    public AladinBookCreator(
            BookWindowService bookWindowService, 
            OclcService oclcService,
            ChatGptService chatGptService) {
        this.bookWindowService = bookWindowService;
        this.oclcService = oclcService;
        this.chatGptService = chatGptService;
    }

    private static String findDescription(Document doc) {
        String result = null;
        for (Element element : doc.getAllElements()) {
            for (Node n : element.childNodes()) {
                if (n.nodeName().equals("#comment")) {
                    if (n.toString().contains("책소개")) {
                        try {
                            result = Jsoup.parse(
                                element.siblingElements().toString())
                                .getElementsByClass("Ere_prod_mconts_R")
                                .first().wholeText().trim();
                        }
                        catch (NullPointerException e) {
                            result = "";
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        var book = new Book();
        var doc = Connect.connectToURL(bookPageUrl);
        if (doc == null)
            throw new IOException(String.format("Document wasn't loaded: %s", bookPageUrl));
        book.setBookPageUrl(bookPageUrl);
        return fillInBasicData(book, doc);
    }

    public Book fillInBasicData(Book book, Document doc) {
        parseIsbn(book, doc);
        book.setkIsbn(parseKisbn(doc));
        book.setOriginalPriceNumber(parsePrice(doc));
        book.setTitle(parseTitle(doc));
        book.setImageURL(parseImageUrl(doc));
        book.setCategory(parseCategory(doc));
        book = parseAuthorDetails(book, doc);
        book = parseSecondDetailSection(book, doc);
        book = setWeight(book);
        book.setLanguageCode("KOR");
        book.setCurrencyType("Won");

        return book;
    }

    private Book parseIsbn(Book book, Document doc) {
        try {
            var isbnString = doc.getElementsByAttributeValueMatching("property", "og:barcode").attr("content");
            book.setIsbnString(isbnString);
            book.setIsbn(Long.parseLong(isbnString));
        }
        catch (NumberFormatException e) {
            book.setIsbn(-1);
            book.setIsbnString(doc.getElementsByAttributeValueMatching("property", "books:isbn").attr("content"));
        }
        finally {
            return book;
        }
    }

    private String parseKisbn(Document doc) {
        return doc.getElementsByAttributeValueMatching("property", "books:isbn").attr("content");
    }

    private int parsePrice(Document doc) {
        try {
            var originalPrice = doc.getElementsByClass("info_list").first().getElementsByClass("Ritem").first().text();
            var sb = new StringBuilder(originalPrice);
            sb.deleteCharAt(sb.length() - 1);
            return NumberFormat.getInstance(Locale.KOREA).parse(sb.toString()).intValue();
        }
        catch (Exception e) {
            return -1;
        }
    }

    private String parseTitle(Document doc) {
        return doc.getElementsByAttributeValueMatching("name", "twitter:title").attr("content");
    }

    private String parseDescription(Book book) {
        var map = new HashMap<String, String>();
        map.put("Referer", book.getBookPageUrl());
        Document doc = null;
        if (book.getkIsbn() != null) {
            doc = Connect.connectToURLwithHeaders(
                createLazyDescriptionUrl(book.getkIsbn()), map);
        }
        else {
            doc = Connect.connectToURLwithHeaders(
                createLazyDescriptionUrl(book.getIsbnString()), map);
        }
        return findDescription(doc);
    }

    private String createLazyDescriptionUrl(String details) {
        return "https://www.aladin.co.kr/shop/product/getContents.aspx?ISBN="
            + details + "&name=Introduce&type=0&date=11";
    }

    private String parseImageUrl(Document doc) {
        return doc.getElementsByAttributeValueMatching(
            "property", "og:image").attr("content");
    }

    private boolean imageCheck(String url) {
        try {
            ImageIO.read(new URL(url));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private String parseCategory(Document doc) {
        try {
            return doc.getElementById("ulCategory").getElementsByTag("li").first().getElementsByTag("a").get(1).text();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Book parseAuthorDetails(Book book, Document doc) {

        try {
            String authorSection = doc.getElementsByClass("Ere_sub2_title").first().text();

            book.setTranslator(FindTranslator(authorSection));
            book.setEnglishTitle(FindOriginalTitle(authorSection));
            book.setPublisher(FindPublisher(authorSection));
            book.setPublishDateFormatted(formatDate(FindPublishDate(authorSection)));
            SetAuthors(book, authorSection);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    private String FindTranslator(String authorSection) {
        var translatorMatcher = TRANSLATOR_PATTERN.matcher(authorSection);
        if (translatorMatcher.find()) {
            var nameMatcher = NAMES_PATTERN.matcher(translatorMatcher.group(0));
            if (nameMatcher.find()) {
                return nameMatcher.group(1);
            }
        }
        return "";
    }

    private String FindOriginalTitle(String authorSection) {
        var m = ORIGINAL_TITLE_PATTERN.matcher(authorSection);
        if (m.find())
            return m.group(1).trim();
        return "";
//        원제 : ([\\p{L} ]+)(?: \\( \\d{4}년\\))?
    }

    private String FindPublisher(String authorSection) {
        var m = PUBLISHER_PATTERN.matcher(authorSection);
        if (m.find())
            return m.group(1);
        return "";
//        "\\)([\\u3131-\\uD79DA-Za-z ]+)(?:\\d{4}-\\d{2}-\\d{2})"
    }

    private String FindPublishDate(String authorSection) {
        var m = PUBLISH_DATE_PATTERN.matcher(authorSection);
        if (m.find())
            return m.group(0);
        return "";
//        "\\d{4}-\\d{2}-\\d{2}"
    }

    private Book SetAuthors(Book book, String authorSection) {
        var authorMatcher = AUTHORS_PATTERN.matcher(authorSection);
        book.setAuthor("");
        book.setAuthor2("");
        if (authorMatcher.find()) {
            var nameMatcher = NAMES_PATTERN.matcher(authorMatcher.group(0));

            if (nameMatcher.find()) {
                book.setAuthor(nameMatcher.group(1));
            }

            if (nameMatcher.find()) {
                book.setAuthor2(nameMatcher.group(1));
            }

            if (nameMatcher.find()) {
                book.setAuthor2("1494");
            }
        }

        return book;
    }

    private String formatDate(String date) {
        StringTokenizer st = new StringTokenizer(date, "-");
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append(st.nextToken());
        sb.insert(0, st.nextToken());
        sb.insert(2, st.nextToken());
        sb.insert(2, '/');
        return sb.toString();
    }

    private Book parseSecondDetailSection(Book book, Document doc) {
        try {
            String bookDetailSection = doc.getElementsByClass("conts_info_list1").first().text();
            var stringTokenizer = new StringTokenizer(bookDetailSection, " ");
            while (stringTokenizer.hasMoreTokens()) {
                String s = stringTokenizer.nextToken();
                if (s.contains("반양장본"))
                    book.setCover("PB");
                else if (s.contains("양장본"))
                    book.setCover("HC");
                else if (s.contains("쪽"))
                    book.setPages(Integer.parseInt(s.substring(0, s.length() - 1)));
                else if (s.contains("m") || s.contains("*"))
                    book.setBookSizeFormatted(formatBookSize(s));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    private String formatBookSize(String size) {
        StringTokenizer st = new StringTokenizer(size, "*m");
        try {
            double first = Double.parseDouble(st.nextToken()) / 10;
            double second = Double.parseDouble(st.nextToken()) / 10;
            if (first % 1 != 0 && second % 1 != 0) {
                return first + " x " + second;
            }
            else if (first % 1 == 0 && second % 1 == 0) {
                return (int) first + " x " + (int) second;
            }
            else if (first % 1 == 0) {
                return (int) first + " x " + second;
            }
            else if (second % 1 == 0) {
                return first + " x " + (int) second;
            }
            else return size;
        }
        catch (NumberFormatException | NoSuchElementException e) {
            return "problem";
        }
    }

    private Book setWeight(Book book) {
        var pages = book.getPages();
        book.setWeight(pages % 300 > 1 ? (pages / 300) + 1 : pages / 300);
        return book;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        if (!imageCheck(book.getImageURL())) {
            book.setImageURL("Problem with image");
        }

        book.setDescription(parseDescription(book));
        scrapeLazyAuthor(book);
        book.setRomanizedTitle(Romanizer.hangulToRoman(book.getTitle()));
        bookWindowService.findIds(book);
        book.setSummary(chatGptService.getSummary(book));
        book.setTranslatedTitle(chatGptService.getTitle(book));
        chatGptService.setCategory(book);
//        if (book.getOclc() < 1) {
//            book.setOclc(oclcService.findOclc(String.valueOf(book.getIsbn())));
//        }

        return book;

    }

    private Book scrapeLazyAuthor(Book book) {
        Map<String, String> map = new HashMap<>();
        map.put("Referer", book.getBookPageUrl());

        var doc = Connect.connectToURLwithHeaders(
            createLazyAuthorUrl(String.valueOf(book.getkIsbn())), map);
        try {
            addPrizes(book, doc);
        } catch (Exception e) {
            LOGGER.error(String.format(
                "Failed to scrape prizes for %s",
                    book.getTitle()));
            LOGGER.error(e.getMessage(), e);
        }

        if (book.getEnglishTitle() == "") {
            book.setAuthorOriginal("");
            return book;
        }
        book.setAuthorOriginal(findAuthorOriginal(doc));
        return book;
    }

    private void addPrizes(Book book, Document doc) {
        var prizeOption = doc.getElementsByClass("conts_info_list2").first()
            .getElementsByTag("ul").first()
            .children().stream()
            .filter(e -> e.wholeText().contains("수상"))
            .findFirst();
        if (prizeOption.isEmpty())
            return;

        var sb = new StringBuilder(book.getDescription());
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        var prizes = prizeOption.get()
            .getElementsByClass("Ere_sub_blue")
            .first().wholeText()
            .trim().split(",");

        ArrayUtils.reverse(prizes);
        Arrays.stream(prizes)
            .map(s -> s.trim())
            .forEach(s -> {
                sb.append(s);
                sb.append(System.lineSeparator());
            });

        book.setDescription(sb.toString());
    }

    private String createLazyAuthorUrl(String details) {
        return String.format("https://www.aladin.co.kr/shop/product/getContents.aspx?ISBN=%s&name=AuthorInfo&type=0&date=13", details);
    }

    private String findAuthorOriginal(Document doc) {
        try {
            String result = doc.getElementsByClass("Ere_fs18 Ere_sub_gray3 Ere_str").first().text().trim();
            String regex = "\\(([^()]+)\\)";
            var m = Pattern.compile(regex).matcher(result);
            if (m.find()) {
                return m.group(1);
            }
            else return "";
        }
        catch (NullPointerException e) {
            LOGGER.info("Couldn't find original Author: " + doc.location());
            return "";
        }
    }

    @Override
    public String BookPagePrefix() {
        return "aladin.co.kr/shop/wproduct.aspx?ItemId";
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        List<Book> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(pageofLinks);
        while (st.hasMoreTokens()) {
            list.add(createBookFromBookPage(st.nextToken()));
        }
        return list;
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkInventoryAndOclc(Book book) {
        if (book.getIsbn() == -1L) {
            book.setOclc(-1L);
            return;
        }
        String isbn = String.valueOf(book.getIsbn());
        book.setTitleExists(bookWindowService.doesBookExist(isbn));
        //todo all oclc is locked down. need to redo this
        //book.setOclc(oclcService.findOclc(isbn));
        book.setOclc(-1L);
    }
}
