package com.aquino.webParser.bookCreators;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class AmazonJapanBookCreatorTest {

    @Test
    public void createBook() {
    }

    @Test
    public void createBookFromBookPage() {
    }

    @Test
    public void JsoupParse() {
        Document doc = Jsoup.parse("<li><b>ムック:</b> 127ページ</li>");
        assertEquals(doc.getElementsByTag("li").first().ownText().trim(), "127ページ" );
        assertNotSame(doc.getElementsByTag("li").first().ownText().trim(), "129ページ" );
    }

    @Test
    public void DateParseTest() {
        String dateSource = "2012/7/27";
        String dataSource2 = "2012/7/7";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/M/d");
        LocalDate date =  LocalDate.parse(dateSource, df);
        assertEquals(date.getMonthValue(), 7);
        assertEquals(date.getYear(), 2012);
        assertEquals(date.getDayOfMonth(), 27);
    }
}