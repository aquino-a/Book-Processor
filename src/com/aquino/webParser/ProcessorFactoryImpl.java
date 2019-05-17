package com.aquino.webParser;

import com.aquino.webParser.bookCreators.BookCreatorType;
import com.aquino.webParser.oclc.OclcServiceImpl;
import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.aladin.AladinBookCreator;
import com.aquino.webParser.oclc.OclcService;

import java.util.HashMap;

public class ProcessorFactoryImpl {

    private BookWindowService bookWindowService;
    private HashMap<BookCreatorType, BookCreator> bookCreatorHashMap = new HashMap<>();
    private OclcService oclcService;

    public BookWindowService CreateWindowService(){
        if(bookWindowService == null)
            bookWindowService = new BookWindowServiceImpl();
        return bookWindowService;
    }

    public BookCreator CreateBookCreator(BookCreatorType creatorType){
        if(!bookCreatorHashMap.containsKey(creatorType)) {
            BookCreator newCreator;
            switch(creatorType){
                case AladinApi: newCreator = new AladinBookCreator(); break;
                case AmazonJapan: newCreator = new AmazonJapanBookCreator(); break;
                default: throw new UnsupportedOperationException(creatorType.toString());
            }
            bookCreatorHashMap.put(creatorType, newCreator);
        }
        return bookCreatorHashMap.get(creatorType);
    }

    public OclcService CreateOclcService(){
        if(oclcService == null)
            oclcService = new OclcServiceImpl();
        return oclcService;
    }


}
