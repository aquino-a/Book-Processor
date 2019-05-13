package com.aquino.webParser.Amazon;

import com.aquino.webParser.Book;
import com.aquino.webParser.Utilities.Connect;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class AmazonJapanBookCreator {


    //https://www.amazon.co.jp/s?k=%s
    private static final String kinoBookUrlFormat = "https://www.kinokuniya.co.jp/f/dsg-01-%s";

    public Book createBookFromBookPage(String bookPage) throws IOException {
        Book book = new Book();
        Document doc = Connect.connectToURL(bookPage);
        if(doc == null)
            throw new IOException(String.format("Document wasn't loaded: %s",bookPage));
        return fillInBasicData(book, doc);

    }

    private Book fillInBasicData(Book book, Document doc) {

        return book;
    }




}
