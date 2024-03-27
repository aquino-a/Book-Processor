package com.aquino.webParser.speed;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

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

    Stream<SpeedBook> download(int page) throws IOException {
        var url = String.format(BEST_URL, category.cid, page);
        var request = connection.newRequest(url);
        var document = request.get();

        return document.getElementsByClass("ss_book_box")
                .stream()
                .map(this::parseBook)
                .filter(this::isNotOld);
    }

    SpeedBook parseBook(Element element) {
        var speedBook = new SpeedBook();

        speedBook.imageUrl(findImageUrl(element));
        speedBook.incompletePublishDate(findIncompletePublishDate(element));
        speedBook.itemUrl(findItemUrl(element));
        speedBook.price(findPrice(element));
        speedBook.title(findTitle(element));
        speedBook.rank(findRank(element));

        return speedBook;
    }

    private String findImageUrl(Element element) {
        var imageElement = element.getElementsByClass("front_cover")
                .first();

        if (imageElement == null) {
            return null;
        }

        return imageElement.attr("src");
    }

    private LocalDate findIncompletePublishDate(Element element) {
        var allText = element.getElementsByClass("ss_book_list")
                .text();

        return extractLocalDate(allText);
    }

    private String findItemUrl(Element element) {
        var urlElement = element.getElementsByClass("bo3")
                .first();

        if (urlElement == null) {
            return null;
        }

        return urlElement.attr("src");
    }

    private int findPrice(Element element) {
        var allText = element.getElementsByClass("ss_book_list")
                .text();

        var prices = extractPrices(allText);
        var firstPrice = prices.findFirst();
        if (firstPrice.isPresent()) {
            return firstPrice.get();
        }

        return -1;
    }

    private String findTitle(Element element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findTitle'");
    }

    private int findRank(Element element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findRank'");
    }

}
