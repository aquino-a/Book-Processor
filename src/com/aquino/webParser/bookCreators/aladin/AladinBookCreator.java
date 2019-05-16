package com.aquino.webParser.bookCreators.aladin;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.BookCreator;

import java.io.IOException;

public class AladinBookCreator implements BookCreator {

    private static final String apiUrlFormat = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=%s&itemIdType=ItemId&ItemId=%s&output=js&OptResult=packing&Cover=Big";
    private String apiKey;

    public Book createBook(String isbn){
        return null;
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        String itemId = parseItemId(bookPageUrl);

        return null;
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
    public Book[] bookArrayFromLink(String pageofLinks) {
        return new Book[0];
    }

    @Override
    public Book[] bookArrayFromIsbn(String pageofIsbns) {
        return new Book[0];
    }

    @Override
    public void checkInventoryAndOclc(Book result) {

    }


    private String parseItemId(String bookPageUrl) {
        return null;
    }
}
