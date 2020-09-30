package com.aquino.webParser;

import com.aquino.webParser.bookCreators.BookCreatorType;
import com.aquino.webParser.bookCreators.honto.HontoBookCreator;
import com.aquino.webParser.bookCreators.honya.HonyaClubBookCreator;
import com.aquino.webParser.oclc.OclcServiceImpl;
import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.aladin.AladinBookCreator;
import com.aquino.webParser.oclc.OclcService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class ProcessorFactoryImpl {

    private BookWindowService bookWindowService;
    private HashMap<BookCreatorType, BookCreator> bookCreatorHashMap = new HashMap<>();
    private OclcService oclcService;
    private ObjectMapper mapper;
    private String aladinApiKey;

    public BookWindowService CreateWindowService(){
        if(bookWindowService == null)
            bookWindowService = new BookWindowServiceImpl();
        return bookWindowService;
    }

    public BookCreator CreateBookCreator(BookCreatorType creatorType) throws IOException {
        if(!bookCreatorHashMap.containsKey(creatorType)) {
            BookCreator newCreator;
            switch(creatorType){
                case AladinApi: newCreator = new AladinBookCreator(getAladinApiKey(), CreateWindowService(), CreateOclcService(),CreateObjectMapper()); break;
                case AmazonJapan: {
                    AmazonJapanBookCreator abc = new AmazonJapanBookCreator(CreateWindowService(), CreateOclcService());
                    abc.setHontoBookCreator(new HontoBookCreator());
                    abc.setHonyaClubBookCreator(new HonyaClubBookCreator());
                    newCreator = abc;

                } break;
                default: throw new UnsupportedOperationException(creatorType.toString());
            }
            bookCreatorHashMap.put(creatorType, newCreator);
        }
        return bookCreatorHashMap.get(creatorType);
    }

    private ObjectMapper CreateObjectMapper() {
        if(mapper == null)
            mapper = new ObjectMapper();
        return mapper;
    }

    public OclcService CreateOclcService(){
        if(oclcService == null)
            oclcService = new OclcServiceImpl();
        return oclcService;
    }

    private String getAladinApiKey() throws IOException {
        if(aladinApiKey == null)
            loadProperties();
        return aladinApiKey;
    }

    private void loadProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(ProcessorFactoryImpl.class.getClassLoader()
                .getResourceAsStream("com/aquino/webParser/resources/config.properties"));
        aladinApiKey = prop.getProperty("aladin.api.key");
    }


}
