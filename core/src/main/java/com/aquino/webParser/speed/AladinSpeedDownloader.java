package com.aquino.webParser.speed;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAmount;
import java.util.regex.Pattern;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

abstract class AladinSpeedDownloader {
    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("(\\d{4})년 ?(\\d{1,2})월");

    private final LocalDate publishDateCutOff;
    int pageCount = 20;

    AladinSpeedDownloader(TemporalAmount publishDateCutOff) {
        this.publishDateCutOff = LocalDate.now().minus(publishDateCutOff);
    }

    boolean isNotOld(SpeedBook book) {
        return book.publishDate().isAfter(publishDateCutOff);
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

    public int pageCount() {
        return pageCount;
    }

    public void pageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
