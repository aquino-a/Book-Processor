package com.aquino.webParser.bookCreators.honya;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class HonyaClubBookCreator implements BookCreator {

    private static final String HONYA_CLUB_URL = "https://www.honyaclub.com";
    private static final String SEARCH_URL_FORMAT = "https://www.honyaclub.com/shop/goods/search.aspx?cat_p=&search=x&keyw=%s";

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
}
