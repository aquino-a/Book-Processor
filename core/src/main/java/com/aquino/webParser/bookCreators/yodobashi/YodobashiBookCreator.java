package com.aquino.webParser.bookCreators.yodobashi;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.utilities.Connect;

public class YodobashiBookCreator implements BookCreator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SEARCH_URL_FORMAT = "https://www.yodobashi.com/?word=%s";

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if (doc == null) {
            throw new IOException(String.format("Yodobashi search page wasn't loaded: %s", isbn));
        }

        var bookUrl = doc.selectFirst("[data-sku] > a")
                .absUrl("href");

        if (StringUtils.isBlank(bookUrl)) {
            throw new IOException(String.format("Book wasn't found on search page %s", isbn));
        }

        Book book = new Book();
        book.setBookPageUrl(bookUrl);
        return book;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        try {
            var bookDoc = Jsoup.connect(book.getBookPageUrl())
                    .get();

            book.setOriginalPrice(parsePrice(bookDoc));
            book.setDescription(parseDescription(bookDoc));
            book.setCategory(parseCategory(bookDoc));

        } catch (Exception e) {
            LOGGER.warn("Problem getting Yododashi price, description and category.", e);
        }

        return book;
    }

    private String parseCategory(Document doc) {
        return doc.select("div.breadcrumbs.js_ctlg_ageconfirm_permit")
                .getFirst()
                .children()
                .stream()
                .map(Element::text)
                .collect(Collectors.joining(" > "));
    }

    private String parseDescription(Document doc) {
        return doc.select("ul.pDescription")
                .getFirst()
                .text();
    }

    private String parsePrice(Document doc) {
        // <meta content="1650" itemprop="price"/>
        return doc.select("meta[itemprop=price]")
                .getFirst()
                .attr("content");
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
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
