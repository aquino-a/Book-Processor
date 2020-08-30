package com.aquino.webParser.bookCreators.amazon;

import com.aquino.webParser.Book;
import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.aladin.AladinBookCreator;
import com.aquino.webParser.oclc.OclcService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
    @Mock
    BookWindowService bookWindowService;
    @Mock
    OclcService oclcService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bc = new AmazonJapanBookCreator(bookWindowService,oclcService);
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
//        assertEquals("",book.getImageURL());
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
        URL url = this.getClass().getResource("test2.html");
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
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/81T5b9dTwkL.jpg",book.getImageURL());
        assertEquals(book.getTranslator(), "井口耕二");
        assertEquals(book.getBookSizeFormatted(), "21 x 14.8");
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
//        assertEquals("",book.getImageURL());
        assertEquals("",book.getTranslator());
        assertEquals("",book.getType());
        assertEquals("旭屋出版",book.getPublisher());
        assertEquals("",book.getType());
        assertEquals("",book.getDescription());
    }

    @Test
    public void basicInfoTestFromPage4() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test4.html");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(9784163907956L,book.getIsbn());
        assertEquals(372,book.getPages());
        assertEquals("02/22/2018",book.getPublishDateFormatted());
        assertEquals("19 x 13.4",book.getBookSizeFormatted());
        assertEquals("【2019年本屋大賞 大賞】そして、バトンは渡された",book.getTitle());
        assertEquals(1728,book.getOriginalPriceNumber());
        assertEquals("瀬尾まいこ",book.getAuthor());
        assertEquals("",book.getAuthor2());
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/61vjYK8JYwL.jpg",book.getImageURL());
        assertEquals("",book.getTranslator());
        assertEquals("文藝春秋",book.getPublisher());
        assertEquals("PB",book.getType());
        assertTrue(book.getDescription().startsWith("内容紹介"));
        assertTrue(book.getDescription().endsWith("表示する"));
    }

    @Test
    public void basicInfoTestFromPage5() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test5.html");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(9784822289607L,book.getIsbn());
        assertEquals(400,book.getPages());
        assertEquals("01/11/2019",book.getPublishDateFormatted());
        assertEquals("21 x 14.8",book.getBookSizeFormatted());
        assertEquals("FACTFULNESS",book.getTitle());
        assertEquals(1944,book.getOriginalPriceNumber());
        assertEquals("1494",book.getAuthor());
        assertEquals("",book.getAuthor2());
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/61G6sKES3eL.jpg",book.getImageURL());
        assertEquals("上杉 周作 & 関 美和",book.getTranslator());
        assertEquals("日経BP社",book.getPublisher());
        assertTrue(book.getDescription().startsWith("内容紹介"));
        assertTrue(book.getDescription().endsWith("表示する"));
    }

    @Test
    public void basicInfoTestFromPage6() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test6.html");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(9784265802364L,book.getIsbn());
        assertEquals(32,book.getPages());
        assertEquals("01/18/2018",book.getPublishDateFormatted());
        assertEquals("28.7 x 24.6",book.getBookSizeFormatted());
        assertEquals("すずちゃんののうみそ 自閉症スペクトラム",book.getTitle());
        assertEquals(1728,book.getOriginalPriceNumber());
        assertEquals("竹山 美奈子",book.getAuthor());
        assertEquals("",book.getAuthor2());
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/81tBP1ukvLL.jpg",book.getImageURL());
        assertEquals("",book.getTranslator());
        assertEquals("岩崎書店",book.getPublisher());
        assertEquals("",book.getType());
        assertTrue(book.getDescription().startsWith("内容紹介"));
        assertTrue(book.getDescription().endsWith("表示する"));
    }

    @Test
    public void basicInfoTestFromPage7() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test7.html");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(book.getIsbn(), 9784488437039L);
        assertEquals(book.getPages(), 448);
        assertEquals(book.getOriginalPriceNumber(), 1056);
        assertEquals(book.getAuthor(), "大阪 圭吉");
        assertEquals(book.getPublishDateFormatted(), "08/12/2020");
        assertEquals(book.getPublisher(), "東京創元社");
        assertEquals(book.getAuthor2(), "");
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/71Dk-7rRZuL.jpg",book.getImageURL());
        assertEquals(book.getTranslator(), "");
        assertEquals(book.getBookSizeFormatted(), "");
        assertEquals(book.getType(), "");
        assertEquals(book.getTitle(), "死の快走船");
        assertTrue(book.getDescription().startsWith("岬に建つ白"));
        assertTrue(book.getDescription().endsWith("り抜く。"));
    }


    @Test
    public void FromIsbnKindleBugFix1() throws IOException, URISyntaxException {

        String isbn = "9784391154511";
        Book book = bc.createBookFromIsbn(isbn);
        assertEquals(book.getIsbn(), 9784391154511L);
        assertEquals(book.getPages(), 127);
        assertEquals(book.getOriginalPriceNumber(), 1485);
        assertEquals(book.getAuthor(), "河井 美歩");
    }

    @Test
    public void FromIsbnKindleBugFix2() throws IOException, URISyntaxException {

        String isbn = "9784864541473";
        Book book = bc.createBookFromIsbn(isbn);
        assertEquals(book.getIsbn(), 9784864541473L);
        assertEquals(book.getPages(), 184);
        assertEquals(book.getOriginalPriceNumber(), 1980);
        assertEquals(book.getAuthor(), "白井 恭弘");
    }

    @Test
    public void japRomanizeTestFromPage() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("test6.html");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals("すずちゃんののうみそ 自閉症スペクトラム",book.getTitle());
        //suzu chan nono umi so jiheishou supekutoramu
        bc.fillInAllDetails(book);
        assertEquals("Suzu chan nono umi so jiheishou supekutoramu", book.getRomanizedTitle());
    }

    @Test
    public void createBookFromBookIsbnTest() throws IOException {

        String isbn = "9784822289607";
        Book book = bc.createBookFromIsbn(isbn);
        assertEquals(9784822289607L,book.getIsbn());
        assertEquals(400,book.getPages());
        assertEquals("01/11/2019",book.getPublishDateFormatted());
        assertEquals("21 x 14.8",book.getBookSizeFormatted());
        assertEquals("FACTFULNESS",book.getTitle());
        assertEquals(1980,book.getOriginalPriceNumber());
        assertEquals("1494",book.getAuthor());
        assertEquals("",book.getAuthor2());
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/81IIRWN-c%2BL.jpg",book.getImageURL());
        assertEquals("上杉 周作 & 関 美和",book.getTranslator());
        assertEquals("日経BP",book.getPublisher());
        assertTrue(book.getDescription().startsWith("内容"));
        assertTrue(book.getDescription().endsWith("続きを読む"));
    }

    @Test
    public void BiggestNumbersBookSizeTest() throws IOException, URISyntaxException {
        Book book = new Book();
        Document doc;
        URL url = this.getClass().getResource("width in middle test.htm");
        File f = new File(url.toURI());
        doc = Jsoup.parse(f,"UTF-8");

        bc.fillInBasicData(book, doc);
        assertEquals(9784087458497L,book.getIsbn());
        assertEquals(360,book.getPages());
        assertEquals("03/20/2019",book.getPublishDateFormatted());
        assertEquals("15.2 x 10.5",book.getBookSizeFormatted());
        assertEquals("裸の華",book.getTitle());
        assertEquals(756,book.getOriginalPriceNumber());
        assertEquals("桜木 紫乃",book.getAuthor());
        assertEquals("",book.getAuthor2());
//        assertEquals("https://images-na.ssl-images-amazon.com/images/I/81tBP1ukvLL.jpg",book.getImageURL());
//        assertEquals("",book.getTranslator());
        assertEquals("集英社",book.getPublisher());
//        assertEquals("",book.getType());
//        assertTrue(book.getDescription().startsWith("内容紹介"));
//        assertTrue(book.getDescription().endsWith("表示する"));
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