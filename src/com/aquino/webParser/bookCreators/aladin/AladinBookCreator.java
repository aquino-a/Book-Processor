package com.aquino.webParser.bookCreators.aladin;

import com.aquino.webParser.Book;

public class AladinBookCreator {

    private static final String apiUrlFormat = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=%s&itemIdType=ItemId&ItemId=%s&output=js&OptResult=packing&Cover=Big";
    private String apiKey;

    public Book createBook(String isbn){
        return null;
    }

    public Book createBookFromWeb(String bookPageUrl){
        String itemId = parseItemId(bookPageUrl);

        return null;
    }

    private String parseItemId(String bookPageUrl) {
        return null;
    }
}
