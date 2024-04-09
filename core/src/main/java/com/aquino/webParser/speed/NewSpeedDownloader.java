package com.aquino.webParser.speed;

import java.util.function.Consumer;

import org.jsoup.Connection;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

public class NewSpeedDownloader extends AladinSpeedDownloader {

    private final static String NEW_URL = "https://www.aladin.co.kr/shop/common/wnew.aspx?ViewRowsCount=50&ViewType=Detail&SortOrder=6&page=%d&BranchType=1&PublishDay=84&CID=%d&NewType=SpecialNew&SearchOption=&CustReviewRankStart=&CustReviewRankEnd=&CustReviewCountStart=&CustReviewCountEnd=&PriceFilterMin=&PriceFilterMax=#";

    public NewSpeedDownloader(
            Connection connection,
            AladinCategory category,
            Consumer<SpeedBook> consumer) {
        super(connection, category, consumer);
        this.pageCount(5);
    }

    @Override
    String searchUrl() {
        return NEW_URL;
    }
}
