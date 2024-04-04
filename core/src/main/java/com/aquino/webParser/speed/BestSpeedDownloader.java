package com.aquino.webParser.speed;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jsoup.Connection;
import org.jsoup.nodes.Element;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

public class BestSpeedDownloader extends AladinSpeedDownloader implements SpeedDownloader {

    private static final String BEST_URL = "https://www.aladin.co.kr/shop/common/wbest.aspx?BestType=Bestseller&BranchType=1&CID=%d&page=%d&cnt=1000&SortOrder=1";

    private final AladinCategory category;

    private final Connection connection;

    private final Consumer<SpeedBook> consumer;

    public BestSpeedDownloader(
            Connection connection,
            TemporalAmount publishDateCutOff,
            AladinCategory category,
            Consumer<SpeedBook> consumer) {
        super(publishDateCutOff);
        this.connection = connection;
        this.category = category;
        this.consumer = consumer;
    }

    @Override
    public void download() throws IOException {
        for (int i = 1; i <= pageCount(); i++) {
            download(i)
                .forEach(consumer);
        }
    }

    Stream<SpeedBook> download(int page) throws IOException {
        var url = String.format(BEST_URL, category.cid, page);
        var request = connection.newRequest(url);
        var document = request.get();

        return document.getElementsByClass("ss_book_box")
                .stream()
                .map(this::parseBook)
                .filter(this::isNotOld)
                .filter(this::isNotExpensive);
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
        var firstSection = element.getElementsByClass("ss_book_list")
                .first();

        if (firstSection == null) {
            return null;
        }

        var titleElement = firstSection.getElementsByClass("bo3")
                .first();

        if (titleElement == null) {
            return null;
        }

        return titleElement.ownText();
    }

    private int findRank(Element element) {
        var firstTd = element.getElementsByTag("tr")
                .first()
                .getElementsByTag("td")
                .first();

        if (firstTd == null) {
            return -1;
        }

        var matcher = NUMBER_PATTERN.matcher(firstTd.ownText());
        if (!matcher.find()) {
            return -1;
        }

        return Integer.parseInt(matcher.group(1));
    }
}
