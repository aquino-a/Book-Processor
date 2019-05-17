package com.aquino.webParser.bookCreators.aladin;

import com.aquino.webParser.Book;
import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.oclc.OclcService;

import java.io.IOException;

public class AladinBookCreator implements BookCreator {

    private static final String apiUrlFormat = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=%s&itemIdType=ItemId&ItemId=%s&output=js&OptResult=packing&Cover=Big";
    private final String apiKey;
    private final BookWindowService bookWindowService;
    private final OclcService oclcService;

    public AladinBookCreator(String apiKey, BookWindowService bookWindowService, OclcService oclcService) {
        this.apiKey = apiKey;
        this.bookWindowService = bookWindowService;
        this.oclcService = oclcService;
    }

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
    public void checkInventoryAndOclc(Book book) {
        if(book.getIsbn() == -1L){
            book.setOclc(-1L);
            return;
        }
        String isbn = String.valueOf(book.getIsbn());
        book.setTitleExists(bookWindowService.doesBookExist(isbn));
        book.setOclc(oclcService.findOclc(isbn));
    }


    private String parseItemId(String bookPageUrl) {
        return null;
    }
}
