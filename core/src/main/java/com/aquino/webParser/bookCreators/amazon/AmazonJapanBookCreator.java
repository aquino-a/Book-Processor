package com.aquino.webParser.bookCreators.amazon;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.honto.HontoBookCreator;
import com.aquino.webParser.bookCreators.honya.HonyaClubBookCreator;
import com.aquino.webParser.bookCreators.kino.KinoBookCreator;
import com.aquino.webParser.bookCreators.worldcat.WorldCatBookCreator;
import com.aquino.webParser.bookCreators.yahoo.YahooBookCreator;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.ExtraInfo;
import com.aquino.webParser.oclc.OclcService;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonJapanBookCreator implements BookCreator {


    private static final String BOOK_PAGE_PREFIX = "https://www.amazon.co.jp";
    //    private static final String searchUrlFormat = "https://www.amazon.co.jp/s?i=stripbooks&rh=p_66%%3A%s&s=relevanceexprank&Adv-Srch-Books-Submit.x=40&Adv-Srch-Books-Submit.y=10&unfiltered=1&ref=sr_adv_b";
    private static final String SEARCH_URL_FORMAT = "https://www.amazon.co.jp/s?k=%%22%s%%22&i=stripbooks&ref=nb_sb_noss";
    private static final String kinoBookUrlFormat = "https://www.kinokuniya.co.jp/f/dsg-01-%s";
    private static final String BOOK_SIZE_FORMAT = "%.1f x %.1f";
    private static final String TRANSLIT_FORMAT = "https://translate.yandex.net/translit/translit?text=%s&lang=ja";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter DATE_SOURCE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d");
    private static final DateTimeFormatter DATE_TARGET_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Pattern IMAGE_URL_SCRIPT_PATTERN = Pattern.compile(
            "'imageGalleryData' : \\[\\{\"mainUrl\":\"(https://m\\.media\\-amazon\\.com/images/I/[A-Za-z0-9%\\-\\+]+\\.jpg)");
    
    private final BookWindowService bookWindowService;
    private final OclcService oclcService;

    private HontoBookCreator hontoBookCreator;
    private HonyaClubBookCreator honyaClubBookCreator;
    private YahooBookCreator yahooBookCreator;
    private WorldCatBookCreator worldCatBookCreator;
    private KinoBookCreator kinoBookCreator;

    public AmazonJapanBookCreator(BookWindowService bookWindowService, OclcService oclcService) {
        this.bookWindowService = bookWindowService;
        this.oclcService = oclcService;
    }

    public static String RomanizeJapanese(String japText) throws IOException {
        Connection c = Jsoup.connect(String.format(TRANSLIT_FORMAT, URLEncoder.encode(japText, StandardCharsets.UTF_8.toString())));
        return c.ignoreContentType(true).execute().body().replaceAll("\"", "");
    }

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        //data-component-id="8"
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if (doc == null) {
            throw new IOException(String.format("Search Document wasn't loaded: %s", isbn));
        }
        Element linkElement;
        linkElement = findLink(doc, "a-size-base a-link-normal s-link-style a-text-bold");

        if (linkElement == null) {
            linkElement = findLink(doc, "a-size-base a-color-base a-link-normal s-underline-text s-underline-link-text s-link-style a-text-bold");
        }

        if(linkElement == null) {
            linkElement = findLink(doc, "a-size-base a-link-normal s-underline-text s-underline-link-text s-link-style a-text-bold");
        }

        if (linkElement == null) {
            throw new NoSuchElementException(String.format("Book link not found for: %s", isbn));
        } else {
            var link = linkElement.attr("href");
            return createBookFromBookPage(BOOK_PAGE_PREFIX + link);
        }
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        Book book = new Book();
        Document doc = Connect.connectToURL(bookPageUrl);
        if (doc == null)
            throw new IOException(String.format("Document wasn't loaded: %s", bookPageUrl));
        book.setBookPageUrl(bookPageUrl);
        return fillInBasicData(book, doc);

    }

    public Book fillInBasicData(Book book, Document doc) {
        book.setOriginalPriceNumber(parsePrice(doc));
        book.setTitle(parseTitle(doc));
        book.setDescription(parseDescription(doc));
        book.setCover(parseType(doc));
        book.setImageURL(parseImageUrl(doc));
        book = parseAuthorDetails(book, doc);
        book = parseSecondDetailSection(book, doc);
        book = setWeight(book);
        book.setCategory(parseCategory(doc));
        book.setLanguageCode("JAP");
        book.setCurrencyType("Yen");

        return book;
    }

    private Element findLink(Document doc, String linkClass) {
        return doc.getElementsByClass(linkClass)
                .stream()
                .filter(e -> e.wholeText().contains("単行本")
                        || e.wholeText().contains("大型本")
                        || e.wholeText().contains("文庫")
                        || e.wholeText().contains("新書"))
                .findFirst()
                .orElse(null);
    }

    private String parseDescription(Document doc) {
        try {
            return doc.getElementById("bookDescription_feature_div")
                    .wholeText().replaceAll("\n", "").trim();
        } catch (Exception e) {
            LOGGER.error(String.format("Couldn't parse description: %s", e.getMessage()));
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }

    //TODO handle original names
    private Book parseAuthorDetails(Book book, Document doc) {
        try {
            Elements es = doc.getElementById("bylineInfo").getElementsByClass("author");
            int authorCount = 0;
            String contributionType = null;
            for (Element e : es) {
                contributionType = e.getElementsByClass("contribution").first()
                        .getElementsByClass("a-color-secondary").first().ownText();
                if (contributionType.contains("翻訳")) {
                    if (book.getTranslator() != null)
                        book.setTranslator(book.getTranslator() + " & " + findContributorName(e));
                    else book.setTranslator(findContributorName(e));
                } else if (contributionType.contains("著")) {
                    if (authorCount == 0) {
                        authorCount++;
                        book.setAuthor(findContributorName(e));
                    } else if (authorCount == 1) {
                        authorCount++;
                        book.setAuthor2(findContributorName(e));
                    } else if (authorCount > 1) {
                        book.setAuthor2("1494");
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(String.format("Couldn't parse author details: %s", e.getMessage()));
            LOGGER.error(e.getMessage(), e);
        } finally {
            setDefaultAutorSection(book);
            return book;
        }
    }

    private void setDefaultAutorSection(Book book) {
        //TODO MORE REQUIRED???
        if (book.getAuthor() == null) book.setAuthor("");
        if (book.getAuthor2() == null) book.setAuthor2("");
        if (book.getTranslator() == null) book.setTranslator("");
        if (book.getEnglishTitle() == null) book.setEnglishTitle("");
        if (book.getAuthorOriginal() == null) book.setAuthorOriginal("");
        if (book.getBookSizeFormatted() == null) book.setBookSizeFormatted("");
    }

    private String findContributorName(Element e) {
        //TODO better way to find using attribute value containing
        Element element = e.getElementsByClass("a-link-normal contributorNameID").first();
        if (notNullorEmpty(element))
            return element.ownText().trim();
        element = e.getElementsByClass("a-link-normal").first();
        if (notNullorEmpty(element))
            return element.ownText().trim();
        LOGGER.warn(String.format("Couldn't parse contributor: %s", e.text()));
        return "";
    }

    private boolean notNullorEmpty(Element e) {
        return e != null && !e.ownText().trim().equals("");
    }

    private int parsePrice(Document doc) {
        try {
            String priceSource = doc.getElementsByClass("a-size-base a-color-price a-color-price").first().text().trim();
            return NumberFormat.getInstance(Locale.JAPAN).parse(priceSource.replace("￥", "").trim()).intValue();
        } catch (NullPointerException | ParseException e) {
            LOGGER.warn(String.format("Couldn't parse price: %s", e.getMessage()));
            return -1;
        }
    }

    private String parseTitle(Document doc) {
        String titleSource = doc.getElementById("productTitle").ownText().trim();
        if (titleSource.indexOf("(") > -1)
            return titleSource.substring(0, titleSource.indexOf("(")).trim();
        else return titleSource;
    }

    private Book parseSecondDetailSection(Book book, Document doc) {
        Elements es = doc.getElementById("detailBullets_feature_div").getElementsByTag("ul").first().getElementsByClass("a-list-item");
        for (Element e : es) {
            String whole = e.wholeText();
            if (whole.contains("ページ")) {
                book.setPages(findPages(e.child(1).ownText()));
            } else if (whole.contains("出版社")) {
                book.setPublisher(findPublisher(e.child(1).ownText().trim()));
            } else if (whole.contains("発売日")) {
                book.setPublishDateFormatted(findPublishedDateFormatted(e.child(1).ownText().trim()));
            } else if (
                    whole.contains("梱包サイズ") ||
                            whole.contains("商品パッケ") ||
                            whole.contains("商品の寸法") ||
                            whole.contains("寸法")
            ) {
                book.setBookSizeFormatted(findBookSizeFormatted(e.child(1).ownText().trim()));
            } else if (whole.contains("ISBN-13")) {
                book.setIsbn(findIsbn(e.child(1).ownText().trim()));
            }
        }
        return book;
    }

    private String parseCategory(Document doc) {
        try {
            return doc.getElementsByClass("a-unordered-list a-nostyle a-vertical zg_hrsr")
                    .first()
                    .wholeText()
                    .strip()
                    .replace("\n\n", "\n")
                    .replaceAll("(-| )", StringUtils.EMPTY);
        } catch (Exception e) {
            LOGGER.error("Problem with amazon category.");
            LOGGER.error(e.getMessage(), e);
            return StringUtils.EMPTY;
        }
    }

    private int findPages(String pagesSource) {
        try {
            return Integer.parseInt(pagesSource.substring(0, pagesSource.indexOf("ページ")));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String findPublisher(String publisherSource) {
        if (publisherSource.contains(";"))
            return publisherSource.substring(0, publisherSource.indexOf(";")).trim();
        else if (publisherSource.contains("("))
            return publisherSource.substring(0, publisherSource.indexOf("(")).trim();
        LOGGER.warn(String.format("Couldn't find publisher: %s", publisherSource));
        return "";
    }

    private String findPublishedDateFormatted(String dateSource) {
        //MM/DD/YYYY
        try {
            LocalDate date = LocalDate.parse(dateSource, DATE_SOURCE_FORMATTER);
            return date.format(DATE_TARGET_FORMATTER);
        } catch (DateTimeParseException e) {
            LOGGER.warn(String.format("Couldn't find publish date: %s", dateSource));
            return "";
        }
    }

    private String findBookSizeFormatted(String sizeSource) {
        //28.5 x 20.9 x 1.4 cm
        String[] nums = sizeSource.split("[ x cm]");
        double[] arr = Arrays.stream(nums).filter(n -> NumberUtils.isCreatable(n))
                .map(Double::valueOf)
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .mapToDouble(Double::doubleValue)
                .toArray();
        return String.format(BOOK_SIZE_FORMAT, arr[0], arr[1]).replace(".0", "");
    }

    private long findIsbn(String isbnSource) {
        try {
            return Long.parseLong(isbnSource.replace("-", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Book setWeight(Book book) {
        int pages = book.getPages();
        if (pages > -1)
            book.setWeight(pages % 300 > 1 ? (pages / 300) + 1 : pages / 300);
        else book.setWeight(-1);
        return book;
    }

    private String parseType(Document doc) {
        try {
            for (Element e : doc.getElementById("title").getElementsByClass("a-size-medium a-color-secondary a-text-normal")) {
                if (e.text().contains("ソフトカバー"))
                    return "PB";
            }
            var ps = doc.getElementById("productSubtitle");
            if (ps.wholeText().contains("ソフトカバー")) {
                return "PB";
            }
            if (ps.wholeText().contains("単行本")) {
                return "HC";
            }
            if (ps.wholeText().contains("新書")) {
                return "PB";
            }
            if (ps.wholeText().contains("文庫")) {
                return "PB";
            }
            return "";
        } catch (Exception e) {
            LOGGER.warn(String.format("Couldn't no book type found: %s", e.getMessage()));
            return "";
        }
    }

    private String parseImageUrl(Document doc) {
        try {
            Element element = doc
                    .getElementById("booksImageBlock_feature_div")
                    .getElementsByTag("script")
                    .first();
            
            return findImageUrl(element.data());
        } catch (Exception e) {
            LOGGER.warn(String.format(
                    "Couldn't find image url: %s", 
                    e.getMessage()));
            
            return "";
        }
    }

    private String findImageUrl(String scriptBody) {
        Matcher m = IMAGE_URL_SCRIPT_PATTERN.matcher(scriptBody);
        if (m.find())
            return m.group(1);
        return "";
    }

    private String removeImageModifier(String imageUrl) {
        StringBuilder sb = new StringBuilder(imageUrl);
        int lastIndex = sb.lastIndexOf(".");
        sb.replace(lastIndex, lastIndex + 1, "$");
        lastIndex = sb.lastIndexOf(".");
        sb.replace(lastIndex + 1, sb.lastIndexOf("$") + 1, "");
        return sb.toString();
    }


    @Override
    public Book fillInAllDetails(Book book) {
        bookWindowService.findIds(book);
        book.setOclc(oclcService.findOclc(String.valueOf(book.getIsbn())));
        book.setRomanizedTitle(lookupRomanizedTitle(book.getTitle()));
        setHonyaDetails(book);
        SetHontoLink(book);
        SetYahooLink(book);
        SetWorldCatLink(book);
        SetKinoLink(book);
        return book;
    }

    private void SetHontoLink(Book book) {
        if (hontoBookCreator == null) {
            return;
        }
        try {
            Book hontoBook = hontoBookCreator.createBookFromIsbn(String.valueOf(book.getIsbn()));
            ExtraInfo ei = new ExtraInfo(45, hontoBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("Honto");
            book.getMiscellaneous().add(ei);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void setHonyaDetails(Book book) {
        if (honyaClubBookCreator == null) {
            return;
        }
        try {
            Book honyaBook = honyaClubBookCreator.createBookFromIsbn(String.valueOf(book.getIsbn()));
            ExtraInfo ei = new ExtraInfo(46, honyaBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("Honya");
            book.getMiscellaneous().add(ei);
            book.setDescription(honyaBook.getDescription());
            book.setCategory2(honyaBook.getCategory());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    private void SetYahooLink(Book book) {
        if (yahooBookCreator == null) {
            return;
        }
        try {
            Book yahooBook = yahooBookCreator.createBookFromIsbn(String.valueOf(book.getIsbn()));
            ExtraInfo ei = new ExtraInfo(47, yahooBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("Yahoo");
            book.getMiscellaneous().add(ei);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void SetWorldCatLink(Book book) {
        if (worldCatBookCreator == null) {
            return;
        }
        try {
            var worldCatBook = worldCatBookCreator.createBookFromIsbn(String.valueOf(book.getIsbn()));
            ExtraInfo ei = new ExtraInfo(48, worldCatBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("OCLC");
            book.getMiscellaneous().add(ei);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void SetKinoLink(Book book) {
        if (kinoBookCreator == null) {
            return;
        }
        try {
            var kinoBook = kinoBookCreator.createBookFromIsbn(String.valueOf(book.getIsbn()));
            ExtraInfo ei = new ExtraInfo(49, kinoBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("Kino");
            book.getMiscellaneous().add(ei);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


    private String lookupRomanizedTitle(String title) {
        if (title == null || title.equals(""))
            return title;
        try {
            var romanized = RomanizeJapanese(title);
            return capitalizeFirstLetter(romanized);
        } catch (IOException e) {
            return "";
        }
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.length() == 0)
            return text;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isAlphabetic(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                break;
            }
        }
        return new String(chars);
    }

    @Override
    public String BookPagePrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        List<Book> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(pageofIsbns);
        while (st.hasMoreTokens()) {
            list.add(createBookFromIsbn(st.nextToken()));
        }
        return list;
    }

    @Override
    public void checkInventoryAndOclc(Book book) {
        if (book.getIsbn() == -1L) {
            book.setOclc(-1L);
            return;
        }
        String isbn = String.valueOf(book.getIsbn());
        book.setTitleExists(bookWindowService.doesBookExist(isbn));
        book.setOclc(oclcService.findOclc(isbn));
    }

    public void setHontoBookCreator(HontoBookCreator hontoBookCreator) {
        this.hontoBookCreator = hontoBookCreator;
    }

    public void setHonyaClubBookCreator(HonyaClubBookCreator honyaClubBookCreator) {
        this.honyaClubBookCreator = honyaClubBookCreator;
    }

    public void setYahooBookCreator(YahooBookCreator yahooBookCreator) {
        this.yahooBookCreator = yahooBookCreator;
    }

    public void setWorldCatBookCreator(WorldCatBookCreator worldCatBookCreator) {
        this.worldCatBookCreator = worldCatBookCreator;
    }

    public void setKinoBookCreator(KinoBookCreator kinoBookCreator) {
        this.kinoBookCreator = kinoBookCreator;
    }
}
