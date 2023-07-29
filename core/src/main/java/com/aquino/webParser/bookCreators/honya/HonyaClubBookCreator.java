package com.aquino.webParser.bookCreators.honya;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

// isbn, title, category 1, cost, description
public class HonyaClubBookCreator implements BookCreator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String HONYA_CLUB_URL = "https://www.honyaclub.com";
    private static final String SEARCH_URL_FORMAT =
        "https://www.honyaclub.com/shop/goods/search.aspx?cat_p=&search=x&keyw=%s";
    private static final Pattern PRICE_PATTERN = Pattern.compile("([\\d,]+)円");

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if(doc == null)
            throw new IOException(String.format("Search Document wasn't loaded: %s",isbn));

        try {
            String bookPageUrl = doc.getElementsByClass("item-img")
                    .first().getElementsByTag("a")
                    .first().attr("href");
            return createBookFromBookPage(HONYA_CLUB_URL.concat(bookPageUrl));
        } catch (Exception e) {
            throw new IOException(String.format("Book page not found: %s",isbn));
        }
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        Book book = new Book();
        book.setBookPageUrl(bookPageUrl);
        Document doc = Connect.connectToURL(bookPageUrl);
        if(doc == null)
            throw new IOException(String.format("Document wasn't loaded: %s",bookPageUrl));
        return fillInBasicData(book, doc);
    }

    public Book fillInBasicData(Book book, Document doc) {
        book.setDescription(doc.getElementsByClass("detail-comment02").first().wholeText());
        book.setOriginalPriceNumber(parsePrice(doc));
        book.setCategory(parseCategory(doc));
        return book;
    }

    private int parsePrice(Document doc) {
        var priceSection = doc.select("dl.item-price")
            .first().wholeText();
        var priceMatcher = PRICE_PATTERN.matcher(priceSection);
        if(priceMatcher.find()){
            return Integer.parseInt(priceMatcher.group(1).strip().replace(",", ""));
        }
        return -1;
    }

    private String parseCategory(Document doc) {
        try {
            return doc.getElementsByClass("navitopicpath_")
                .first()
                .wholeText()
                .strip();
        }
        catch (Exception e) {
            LOGGER.error("Problem with honya category");
            LOGGER.error(e.getMessage(), e);
            return StringUtils.EMPTY;
        }
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
}
