package com.aquino.webParser.Kinokuniya;

import com.aquino.webParser.Book;
import com.aquino.webParser.Utilities.Connect;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class KinoBookCreator {

    private static final String kinoBookUrlFormat = "https://www.kinokuniya.co.jp/f/dsg-01-%s";

    public Book createBook(String isbn) throws IOException {
        Book book = new Book(null);
        Document doc = Connect.connectToURL(String.format(kinoBookUrlFormat, isbn));
        if(doc == null)
            throw new IOException(String.format("Document wasn't loaded: %s",isbn));
        return fillInBasicData(book, doc);

    }

    private Book fillInBasicData(Book book, Document doc) {
        parseAuthorSection(book,doc);
        parseDetailSection(book,doc);
        return book;
    }

    private Book parseAuthorSection(Book book, Document doc) {
        String text = doc.getElementsByClass("infobox ml10 mt10").first().text();
        return book;
    }

    private Book parseDetailSection(Book book, Document doc) {
        return book;
    }




}
