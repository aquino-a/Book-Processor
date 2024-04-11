package com.aquino.webParser.speed;

import java.util.function.Consumer;

import org.jsoup.Connection;

public class BestSpeedDownloader extends AladinSpeedDownloader {

    private static final String BEST_URL = "https://www.aladin.co.kr/shop/common/wbest.aspx?BestType=Bestseller&BranchType=1&CID=%d&page=%d&cnt=1000&SortOrder=1";

    public BestSpeedDownloader(
            Connection connection,
            Consumer<SpeedBook> consumer) {
        super(connection, consumer);
    }

    @Override
    String searchUrl() {
        return BEST_URL;
    }
}
