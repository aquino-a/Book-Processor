package com.aquino.webParser.speed;

import java.util.function.Consumer;

import org.jsoup.Connection;

public class NewSpeedDownloader extends AladinSpeedDownloader {

    private final static String NEW_URL = "https://www.aladin.co.kr/shop/common/wnew.aspx?ViewRowsCount=50&ViewType=Detail&SortOrder=6&page=%d&BranchType=1&PublishDay=84&CID=%d&NewType=SpecialNew&SearchOption=&CustReviewRankStart=&CustReviewRankEnd=&CustReviewCountStart=&CustReviewCountEnd=&PriceFilterMin=&PriceFilterMax=#";

    public NewSpeedDownloader(
            Connection connection,
            Consumer<SpeedBook> consumer) {
        super(connection, consumer);
        this.pageCount(5);
    }

    @Override
    String searchUrl() {
        return NEW_URL;
    }
}
