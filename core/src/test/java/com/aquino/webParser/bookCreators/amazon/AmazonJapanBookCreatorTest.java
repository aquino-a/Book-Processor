package com.aquino.webParser.bookCreators.amazon;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.oclc.OclcService;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URISyntaxException;


public class AmazonJapanBookCreatorTest {

    private AmazonJapanBookCreator bc;
    @Mock
    BookWindowService bookWindowService;
    @Mock
    OclcService oclcService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bc = new AmazonJapanBookCreator(bookWindowService, oclcService);
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
        assertThat(book, is(notNullValue()));
    }

    @Test
    public void createBookFromBookIsbnTest() throws IOException {

        String isbn = "9784822289607";
        Book book = bc.createBookFromIsbn(isbn);
        assertEquals(9784822289607L, book.getIsbn());
        assertEquals(400, book.getPages());
        assertEquals("01/11/2019", book.getPublishDateFormatted());
        assertEquals("21 x 14.8", book.getBookSizeFormatted());
        assertEquals("FACTFULNESS", book.getTitle());
        assertEquals(1782, book.getOriginalPriceNumber());
        assertEquals("ハンス・ロスリング", book.getAuthor());
        assertEquals("1494", book.getAuthor2());
        assertEquals("上杉 周作 & 関 美和", book.getTranslator());
        assertEquals("日経BP", book.getPublisher());
        assertThat(book.getImageURL(), startsWith("https://m.media-amazon.com/images/I/"));
        assertThat(book.getCategory(), is(not(emptyOrNullString())));
        assertThat(book.getDescription(), is(not(emptyOrNullString())));
    }
}