package com.aquino.webParser.speed;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAmount;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

abstract class AladinSpeedDownloader {
    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("(\\d{4})년 ?(\\d{1,2})월", Pattern.MULTILINE);
    private static final Pattern KOREAN_PRICE_PATTERN = Pattern.compile("((?:\\d+,?)+)원", Pattern.MULTILINE);

    private final LocalDate publishDateCutOff;
    private int pageCount = 20;
    private int priceCutoff;

    AladinSpeedDownloader(TemporalAmount publishDateCutOff) {
        this.publishDateCutOff = LocalDate.now().minus(publishDateCutOff);
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
}
