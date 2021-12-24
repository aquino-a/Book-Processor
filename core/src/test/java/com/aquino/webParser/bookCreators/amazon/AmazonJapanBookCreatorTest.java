package com.aquino.webParser.bookCreators.amazon;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.oclc.OclcService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
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
    public void fromIsbnKindleBugFix1() throws IOException, URISyntaxException {

        String isbn = "9784391154511";
        Book book = bc.createBookFromIsbn(isbn);
        assertEquals(book.getIsbn(), 9784391154511L);
        assertEquals(book.getPages(), 127);
        assertEquals(book.getOriginalPriceNumber(), 1485);
        assertEquals(book.getAuthor(), "河井 美歩");
    }

    @Test
    public void fromIsbnKindleBugFix2() throws IOException, URISyntaxException {

        String isbn = "9784864541473";
        Book book = bc.createBookFromIsbn(isbn);
        assertEquals(book.getIsbn(), 9784864541473L);
        assertEquals(book.getPages(), 184);
        assertEquals(book.getOriginalPriceNumber(), 1980);
        assertEquals(book.getAuthor(), "白井 恭弘");
    }

    @Test
    public void bookLinkBugFix() throws IOException, URISyntaxException {

        String isbn = "9784065258910";
        Book book = bc.createBookFromIsbn(isbn);
        assertNotNull(book);
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
        assertEquals("ハンス・ロスリング",book.getAuthor());
        assertEquals("1494",book.getAuthor2());
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/818RNdEODLL.jpg",book.getImageURL());
        assertEquals("上杉 周作 & 関 美和",book.getTranslator());
        assertEquals("日経BP",book.getPublisher());
        assertEquals("1位国際政治情勢\n1位社会一般関連書籍\n1位経営学(本)",book.getCategory());
        assertTrue(book.getDescription().startsWith("ファクトフルネスとは――データや事実にもとづき、"));
        assertTrue(book.getDescription().endsWith("書かれている。"));
    }
}