package com.aquino.webParser.bookCreators.kino;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.bookCreators.honto.HontoBookCreator;
import com.aquino.webParser.bookCreators.honya.HonyaClubBookCreator;
import com.aquino.webParser.bookCreators.yahoo.YahooBookCreator;
import com.aquino.webParser.chatgpt.ChatGptService;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.ExtraInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

// isbn, title, author, publisher, picture, page count
public class KinoBookCreator implements BookCreator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String KINO_URL = "https://www.kinokuniya.co.jp";
    private static final String KINO_ISBN_URL = "https://www.kinokuniya.co.jp/f/dsg-01-%s";
    private static final Pattern PAGES_PATTERN = Pattern.compile("(?:ページ数)? ?(\\d+) ?p");

    private final BookWindowService bookWindowService;
    private final ChatGptService chatGptService;
    private final HonyaClubBookCreator honya;
    private final HontoBookCreator honto;

    private YahooBookCreator yahoo;

    private Map<String, String> cookies;

    public KinoBookCreator(
            BookWindowService bookWindowService,
            ChatGptService chatGptService,
            HontoBookCreator honto,
            HonyaClubBookCreator honya) {
        this.bookWindowService = bookWindowService;
        this.chatGptService = chatGptService;
        this.honto = honto;
        this.honya = honya;
    }

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        return createBookFromBookPage(String.format(KINO_ISBN_URL, isbn));
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        var book = new Book();
        book.setBookPageUrl(bookPageUrl);

        var doc = getDoc(bookPageUrl);

        fillInBasicData(book, doc);

        return book;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        book.setTitleExists(bookWindowService.doesBookExist(String.valueOf(book.getIsbn())));
        if (book.isTitleExists()) {
            // already in bookswindow so it wont be used.
            return book;
        }

        bookWindowService.findIds(book);
        book.setSummary(chatGptService.getSummary(book));
        book.setTranslatedTitle(chatGptService.getTitle(book));
        book.setRomanizedTitle(lookupRomanizedTitle(book.getTitle()));

        // 3.5 not accurate
        // chatGptService.setCategory(book);
        setHonyaDetails(book);
        setHontoDetails(book);
        setYahooLink(book);
        setAmazonLink(book);

        return book;
    }

    private void setAmazonLink(Book book) {
        ExtraInfo ei = new ExtraInfo(
                48,
                String.format(
                        "https://www.amazon.co.jp/s?k=%%22%s%%22&i=stripbooks&ref=nb_sb_noss",
                        book.getIsbn()),
                ExtraInfo.Type.HyperLink);
        ei.setName("Amazon");
        book.getMiscellaneous().add(ei);
    }

    @Override
    public String BookPagePrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkInventoryAndOclc(Book result) {
        throw new UnsupportedOperationException();
    }

    // isbn, title, author, publisher, picture, page count
    private void fillInBasicData(Book book, Document doc) {
        book.setIsbn(findIsbn(doc));
        book.setTitle(findTitle(doc));

        var authors = findAuthors(doc);
        book.setAuthor(authors.getKey());
        book.setAuthor2(authors.getValue());

        book.setPublisher(findPublisher(doc));
        book.setImageURL(findImage(doc));
        book.setPages(findPages(doc));
        book = AmazonJapanBookCreator.setWeight(book);

        book.setLanguageCode("JAP");
        book.setCurrencyType("Yen");
    }

    // <input type="hidden" name="GOODS_STK_NO" value="9784022518965" />
    private long findIsbn(Document doc) {
        try {
            var value = doc.getElementsByAttributeValueMatching("name", "GOODS_STK_NO")
                    .attr("value");

            return Long.parseLong(value);
        } catch (Exception e) {
            LOGGER.error("Couldn't get isbn", e);
            return -1L;
        }
    }

    // <meta property="og:title" content="ふるさとに風が吹く―福島からの発信と地域ブランディングの明日" />
    private String findTitle(Document doc) {
        try {
            return doc.getElementsByAttributeValueMatching("property", "og:title")
                    .attr("content");
        } catch (Exception e) {
            LOGGER.error("Couldn't get title", e);
            return null;
        }
    }

    // <div class="infobox ml10 mt10">
    // <ul>
    // <li><a
    // href="https://www.kinokuniya.co.jp/disp/CSfDispListPage_001.jsp?qsd=true&ptk=01&author=%E7%AE%AD%E5%86%85+%E9%81%93%E5%BD%A6">箭内
    // 道彦</a>/<a
    // href="https://www.kinokuniya.co.jp/disp/CSfDispListPage_001.jsp?qsd=true&ptk=01&author=%E6%B2%B3%E5%B0%BB+%E4%BA%A8%E4%B8%80">河尻
    // 亨一【著】</a></li>
    // <li><input type="button" name="" value="著者名をお気に入りに登録する"
    // onclick="goFavoriteAuthor(document.goodsForm);" class="favAuthorButton"></li>
    // <li itemprop="offerDetails" itemscope
    // itemtype="http://data-vocabulary.org/Offer">価格 <span itemprop="currency"
    // content="JPY"><span class="sale_price" itemprop="price"
    // content="2,420">&yen;2,420</span></span>（本体&yen;2,200）</li>
    // <li>
    // <a
    // href="https://www.kinokuniya.co.jp/disp/CSfDispListPage_001.jsp?qsd=true&ptk=01&publisher-key=%E6%9C%9D%E6%97%A5%E6%96%B0%E8%81%9E%E5%87%BA%E7%89%88">朝日新聞出版</a>（2023/05発売）</li>
    // <li>ポイント <span class="redhot">22pt</span></li>
    // </ul>
    // </div>
    private Entry<String, String> findAuthors(Document doc) {

        try {
            List<String> authorList = doc.getElementsByClass("infobox ml10 mt10")
                    .first()
                    .getElementsByTag("li")
                    .first()
                    .getElementsByTag("a")
                    .stream()
                    .map(Element::ownText)
                    .map(String::trim)
                    .map(author -> author.replace("【著】", StringUtils.EMPTY))
                    .collect(Collectors.toList());

            if (authorList.size() > 1) {
                return Map.entry(authorList.get(0), authorList.get(1));
            } else if (authorList.size() > 0) {
                return Map.entry(authorList.get(0), StringUtils.EMPTY);
            } else {
                throw new RuntimeException("no author <a> tags.");
            }

        } catch (Exception e) {
            LOGGER.error("Couldn't get author", e);
            return Map.entry( StringUtils.EMPTY,  StringUtils.EMPTY);
        }
    }

    private String findPublisher(Document doc) {
        try {
            return doc.getElementsByClass("infobox ml10 mt10")
                    .first()
                    .getElementsByTag("li")
                    .get(3)
                    .getElementsByTag("a")
                    .first()
                    .ownText()
                    .trim();
        } catch (Exception e) {
            LOGGER.error("Couldn't get publisher", e);
            return null;
        }
    }

    // <div class="left_box">
    // <p>
    // <a href="../images/goods/ar2/web/imgdata2/large/40225/4022518960.jpg"
    // target="_blank">
    // <img itemprop="image" onerror="alterImage(this,
    // '../images/web/nowprint.gif')"
    // src="../images/goods/ar2/web/imgdata2/40225/4022518960.jpg"
    // alt="ふるさとに風が吹く―福島からの発信と地域ブランディングの明日" width="195" />
    // </a>
    // </p>
    private String findImage(Document doc) {
        try {
            var relativeUrl = doc.getElementsByClass("left_box")
                    .first()
                    .getElementsByTag("a")
                    .first()
                    .attr("href");

            return KINO_URL + relativeUrl.substring(2);
        } catch (Exception e) {
            LOGGER.error("Couldn't get image", e);
            return null;
        }
    }

    // <div class="infbox dotted ml10 mt05 pt05">
    // <ul>
    // <li>
    // サイズ 46判／ページ数 272p／高さ 19cm</li>
    private int findPages(Document doc) {
        try {
            var pageLi = doc.getElementsByClass("infbox dotted ml10 mt05 pt05")
                    .first()
                    .getElementsByTag("li")
                    .first();
            var matcher = PAGES_PATTERN.matcher(pageLi.ownText());
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            } else {
                throw new RuntimeException("page regex didn't match.");
            }

        } catch (Exception e) {
            LOGGER.error("Couldn't get pages", e);
            return -1;
        }
    }

    private String lookupRomanizedTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return title;
        }

        try {
            var romanized = AmazonJapanBookCreator.RomanizeJapanese(title);

            return AmazonJapanBookCreator.capitalizeFirstLetter(romanized);
        } catch (IOException e) {
            LOGGER.error("Couldn't romanize title", e);
            return "";
        }
    }

    private void setHonyaDetails(Book book) {
        try {
            var honyaBook = honya.createBookFromIsbn(String.valueOf(book.getIsbn()));
            var ei = new ExtraInfo(46, honyaBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("Honya");
            book.getMiscellaneous().add(ei);

            book.setCategory(honyaBook.getCategory());
            book.setOriginalPriceNumber(honyaBook.getOriginalPriceNumber());
            book.setDescription(honyaBook.getDescription());
        } catch (IOException e) {
            LOGGER.error("Problem setting Honya details.", e);
            return;
        }
    }

    private void setHontoDetails(Book book) {
        try {
            Book hontoBook = honto.createBookFromIsbn(String.valueOf(book.getIsbn()));

            ExtraInfo ei = new ExtraInfo(45, hontoBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("Honto");
            book.getMiscellaneous().add(ei);

            book.setAgeGroup(hontoBook.getAgeGroup());
            book.setPublishDateFormatted(hontoBook.getPublishDateFormatted());
            book.setBookSizeFormatted(hontoBook.getBookSizeFormatted());
        } catch (IOException e) {
            LOGGER.error("Problem setting Honto details.", e);
            return;
        }
    }

    private void setYahooLink(Book book) {
        if (yahoo == null) {
            return;
        }

        try {
            Book yahooBook = yahoo.createBookFromIsbn(String.valueOf(book.getIsbn()));
            ExtraInfo ei = new ExtraInfo(47, yahooBook.getBookPageUrl(), ExtraInfo.Type.HyperLink);
            ei.setName("Yahoo");
            book.getMiscellaneous().add(ei);
        } catch (IOException e) {
            LOGGER.error("Problem setting Yahoo details.", e);
            return;
        }
    }

    private Document getDoc(String bookPageUrl) throws IOException {
        if (cookies == null) {
            cookies = getCookies();
        }

        return Jsoup.connect(bookPageUrl)
                .cookies(cookies)
                .get();
    }

    private Map<String, String> getCookies() throws IOException {

        var response = Jsoup.connect(KINO_URL)
                .execute();

        return response.cookies();
    }

    public YahooBookCreator getYahoo() {
        return yahoo;
    }

    public void setYahoo(YahooBookCreator yahoo) {
        this.yahoo = yahoo;
    }
}
