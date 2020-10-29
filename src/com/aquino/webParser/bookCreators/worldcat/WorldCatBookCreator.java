package com.aquino.webParser.bookCreators.worldcat;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class WorldCatBookCreator implements BookCreator {

    private static final String SEARCH_URL_FORMAT = "https://www.worldcat.org/search?qt=worldcat_org_all&q=%s";
    private static final String WORLD_CAT_URL = "https://www.worldcat.org";
    private static final Pattern PUBLISHER_REGEX = Pattern.compile("[\\u0100-\\uFFFFA-Za-z]+ : ([\\w\\u0100-\\uFFFF ]+), [0-9]{4}");
    public static final String NOT_FOUND = "Not Found";

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

    public Book fillInBasicData(Book book, Document doc) {
        book.setAuthor(parseAuthor(doc));
        book.setPublisher(parsePublisher(doc));
        book.setTitle(parseTitle(doc));
        return book;
    }

    private String parseAuthor(Document doc) {

        var sb = new StringBuilder();
        try {
            doc.getElementById("bib-author-cell")
                    .getElementsByAttributeValue("title", "Search for more by this author")
                    .stream()
                    .map(e -> e.ownText().trim())
                    .forEach( s -> { sb.append(s); sb.append(','); });
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (Exception e) {
            return NOT_FOUND;
        }
    }

    private String parsePublisher(Document doc) {

        try {

            var matcher =  PUBLISHER_REGEX.matcher(doc.getElementById("bib-publisher-cell")
                    .ownText());
            if(matcher.find())
                return matcher.group(1).trim();
            else return NOT_FOUND;
        } catch (Exception e) {
            return NOT_FOUND;
        }
    }

    private String parseTitle(Document doc) {

        try {
            return doc.getElementById("bibdata")
                    .getElementsByClass("title")
                    .first()
                    .ownText().trim();
        } catch (Exception e) {
            return NOT_FOUND;
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
