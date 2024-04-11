package com.aquino.webParser.speed;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.TemporalAmount;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jsoup.Connection;
import org.jsoup.nodes.Element;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

abstract class AladinSpeedDownloader implements SpeedDownloader {
    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("(\\d{4})년 ?(\\d{1,2})월", Pattern.MULTILINE);
    private static final Pattern KOREAN_PRICE_PATTERN = Pattern.compile("((?:\\d+,?)+)원", Pattern.MULTILINE);
    static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

    private final Connection connection;
    private final Consumer<SpeedBook> consumer;
    private LocalDate publishDateCutOff;
    private int pageCount = 20;
    private int priceCutoff;

    public AladinSpeedDownloader(
            Connection connection,
            Consumer<SpeedBook> consumer) {
        this.consumer = consumer;
        this.connection = connection;
        this.publishDateCutOff(Period.ofMonths(5));
        this.priceCutoff(50000);
    }

    abstract String searchUrl();

    @Override
    public void download(AladinCategory category) throws IOException {
        for (int i = 1; i <= pageCount(); i++) {
            download(category, i)
                    .forEach(consumer);
        }
    }

    boolean isNotOld(SpeedBook book) {
        return book.publishDate().isAfter(publishDateCutOff);
    }

    boolean isNotExpensive(SpeedBook book) {
        return book.price() <= priceCutoff;
    }

    LocalDate extractLocalDate(String incompletePublishString) {
        var matcher = YEAR_MONTH_PATTERN.matcher(incompletePublishString);
        if (!matcher.find()) {
            return LocalDate.now().minusYears(100);
        }

        var year = Integer.parseInt(matcher.group(1));
        var month = Integer.parseInt(matcher.group(2));

        var yearMonth = YearMonth.of(year, month);

        return yearMonth.atEndOfMonth();
    }

    Stream<Integer> extractPrices(String textWithPrices) {
        var matcher = KOREAN_PRICE_PATTERN.matcher(textWithPrices);

        return matcher.results()
                .skip(1)
                .map(mr -> mr.group(1))
                .map(n -> n.replace(",", " "))
                .map(Integer::parseInt);
    }

    Stream<SpeedBook> download(AladinCategory category, int page) throws IOException {
        var url = String.format(searchUrl(), category.cid, page);
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

    public int pageCount() {
        return pageCount;
    }

    public void pageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int priceCutoff() {
        return priceCutoff;
    }

    public void priceCutoff(int priceCutoff) {
        this.priceCutoff = priceCutoff;
    }

    public void publishDateCutOff(TemporalAmount amount) {
        this.publishDateCutOff = LocalDate.now().minus(amount);
    }

    public LocalDate publishDateCutOff() {
        return this.publishDateCutOff;
    }
}
