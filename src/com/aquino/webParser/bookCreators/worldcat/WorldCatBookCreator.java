package com.aquino.webParser.bookCreators.worldcat;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;
import javafx.beans.binding.Bindings;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class WorldCatBookCreator implements BookCreator {

    private static final String SEARCH_URL_FORMAT = "https://www.worldcat.org/search?qt=worldcat_org_all&q=%s";
    private static final String WORLD_CAT_URL = "https://www.worldcat.org";
    private static final Pattern PUBLISHER_REGEX = Pattern.compile("[\\u0100-\\uFFFFA-Za-z]+ : ([\\w]+), [0-9]{4}", Pattern.LITERAL);


    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if(doc == null)
            throw new IOException(String.format("Search Document wasn't loaded: %s",isbn));

        try {
            String bookPageUrl = doc.getElementById("result-1").attr("href");
            return createBookFromBookPage(WORLD_CAT_URL.concat(bookPageUrl));
        } catch (Exception e) {
            throw new IOException(String.format("Book page not found: %s",isbn));
        }
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        Book book = new Book();
        Document doc = Connect.connectToURL(bookPageUrl);
        if(doc == null) {
            throw new IOException(String.format("Book page wasn't loaded: %s", bookPageUrl));
        }
        book.setBookPageUrl(bookPageUrl);
        return fillInBasicData(book, doc);
    }

    private Book fillInBasicData(Book book, Document doc) {
        book.setAuthor(parseAuthor(doc));
        book.setPublisher(parsePublisher(doc));
        book.setTitle(parseTitle(doc));
        return book;
    }

    private String parseAuthor(Document doc) {
    }

    private String parsePublisher(Document doc) {
    }

    private String parseTitle(Document doc) {
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
