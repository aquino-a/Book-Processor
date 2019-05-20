package com.aquino.webParser.bookCreators.amazon;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.aladin.AladinBookCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class AmazonJapanBookCreatorTest {

    private AmazonJapanBookCreator bc;

    @Before
    public void setUp() throws Exception {
        bc = new AmazonJapanBookCreator(null,null);
    }

    @Test
    public void basicInfoTestFromPage1() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test1.mhtml");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(book.getIsbn(), 9784344034082L);
        assertEquals(book.getPages(), 254);
        assertEquals(book.getOriginalPriceNumber(), 1512);
        assertEquals(book.getAuthor(), "前田 裕二");
        assertEquals(book.getPublishDateFormatted(), "12/24/2018");
        assertEquals(book.getPublisher(), "幻冬舎");
        assertEquals(book.getAuthor2(), "");
        assertEquals(book.getTranslator(), "");
        assertEquals(book.getBookSizeFormatted(), "18.6 x 12.8");
        assertEquals(book.getType(), "");
        assertEquals(book.getTitle(), "メモの魔力 The Magic of Memos");
        assertTrue(book.getDescription().startsWith("内容紹介"));
        assertTrue(book.getDescription().endsWith("表示する"));
    }

    @Test
    public void basicInfoTestFromPage2() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test2.mhtml");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(book.getIsbn(), 9784866511139L);
        assertEquals(book.getPages(), 320);
        assertEquals(book.getOriginalPriceNumber(), 1998);
        assertEquals(book.getAuthor(), "ローレンス・レビー");
        assertEquals(book.getPublishDateFormatted(), "03/15/2019");
        assertEquals(book.getPublisher(), "文響社");
        assertEquals(book.getAuthor2(), "");
        assertEquals(book.getTranslator(), "井口耕二");
        assertEquals(book.getBookSizeFormatted(), "14.8 x 2.7");
        assertEquals(book.getType(), "PB");
        assertEquals(book.getTitle(), "PIXAR <ピクサー> 世界一のアニメーション企業の今まで語られなかったお金の話");
        assertTrue(book.getDescription().startsWith("内容紹介"));
        assertTrue(book.getDescription().endsWith("表示する"));
    }

    @Test
    public void basicInfoTestFromPage3() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test3.mhtml");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(9784751109854L,book.getIsbn());
        assertEquals(127,book.getPages());
        assertEquals("07/27/2012",book.getPublishDateFormatted());
        assertEquals("28.5 x 20.9",book.getBookSizeFormatted());
        assertEquals("Pizza!Pizza!Pizza!―行列ピッツェリアの、メニューと考え方",book.getTitle());
        assertEquals(2700,book.getOriginalPriceNumber());
        assertEquals("",book.getAuthor());
        assertEquals("",book.getAuthor2());
        assertEquals("",book.getTranslator());
        assertEquals("",book.getType());
        assertEquals("旭屋出版",book.getPublisher());
        assertEquals("",book.getType());
        assertEquals("",book.getDescription());
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

    @Test
    public void StreamTest() {
        int[] ia = {1, 2, 3};

        try {
            Stream.of(ia).forEach((ii) -> {throw new NumberFormatException(); });
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(true);
        }
    }
}