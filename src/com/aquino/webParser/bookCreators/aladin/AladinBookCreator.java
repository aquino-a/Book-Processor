package com.aquino.webParser.bookCreators.aladin;

import com.aquino.webParser.Book;
import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.oclc.OclcService;
import com.aquino.webParser.utilities.Connect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.Jsoup;

import java.io.IOException;

public class AladinBookCreator implements BookCreator {

    private static final String apiUrlFormat = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=%s&itemIdType=ItemId&ItemId=%s&output=js&OptResult=packing&Cover=Big";
    private final String apiKey;
    private final BookWindowService bookWindowService;
    private final OclcService oclcService;
    private final ObjectMapper mapper;

    public AladinBookCreator(String apiKey, BookWindowService bookWindowService, OclcService oclcService, ObjectMapper mapper) {
        this.apiKey = apiKey;
        this.bookWindowService = bookWindowService;
        this.oclcService = oclcService;
        this.mapper = mapper;
    }

    @Override
    public Book createBookFromIsbn(String isbn){
        return null;
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        String itemId = parseItemId(bookPageUrl);
        //TODO check item id
        String json = Jsoup.connect(String.format(apiUrlFormat, apiKey, itemId)).execute().body();
        return mapper.readValue(json, AladinApiResult.class).getResult().asBook();
    }

    @Override
    public Book fillInAllDetails(Book book) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public String BookPagePrefix() {
        return "aladin.co.kr/shop/wproduct.aspx?ItemId";
    }

    @Override
    public Book[] bookArrayFromLink(String pageofLinks) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public Book[] bookArrayFromIsbn(String pageofIsbns) {
        throw new UnsupportedOperationException();
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
