package com.aquino.webParser.speed;

import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

public class BestSpeedDownloader extends AladinSpeedDownloader implements SpeedDownloader {
    
    private static final String BEST_URL = "https://www.aladin.co.kr/shop/common/wbest.aspx?BestType=Bestseller&BranchType=1&CID=%d&page=%d&cnt=1000&SortOrder=1";

    private final AladinCategory category;

    private final Connection connection;

    public BestSpeedDownloader(Connection connection, TemporalAmount publishDateCutOff, AladinCategory category) {
        super(publishDateCutOff);
        this.connection = connection;
        this.category = category;
    }

    @Override
    public void download() {

    }

    Stream<SpeedBook> download(int page) {
        var url = String.format(BEST_URL, category.cid, page);
        var request = connection.newRequest(url);
        
    }

}
