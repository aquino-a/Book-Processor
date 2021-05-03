package com.aquino.webParser.bookCreators.aladin.web;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.oclc.OclcService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@RunWith(Parameterized.class)
public class AladinBookCreatorTest {

    private final Document doc;
    private final Book expected;
    @Mock
    BookWindowService bookWindowService;
    @Mock
    OclcService oclcService;
    private AladinBookCreator bookCreator;
    public AladinBookCreatorTest(Document doc, Book expected) {
        this.doc = doc;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{index}: {1}")
    public static Iterable<Object[]> bookData() throws IOException, URISyntaxException {
        return Arrays.asList(
            new Object[][]{
                {createDocument("future.html"), createFutureBookTest() },
                {createDocument("rural-setting.html"), createRuralSettingsBookTest() },
                {createDocument("social.html"), createSocialBookTest() },
            }
        );
    }

    /**
     * Gets the expected values for the test of the book called '공간의 미래'
     * Tests a regular book.
     *
     * @return {@link Book} containing the expected values.
     */
    private static Book createFutureBookTest() {
        var book = new Book();
        book.setAuthor("유현준");
        book.setTitle("공간의 미래");
        book.setPublishDateFormatted("04/25/2021");
        book.setIsbn(9788932474427L);
        book.setPages(364);
        book.setPublisher("을유문화사");
        book.setBookSizeFormatted("14.2 x 19.5");
        book.setOriginalPriceNumber(16000);
        book.setImageURL("https://image.aladin.co.kr/product/26915/9/cover500/8932474427_1.jpg");
        book.setAuthor2("");
        book.setTranslator("");
        book.setDescription("우리가 사는 공간은 그 안에 사는 인간의");
        book.setEnglishTitle("");
        return book;
    }

    /**
     * Gets the expected values for the test of the book called '디테일 사전 : 시골 편'
     * Tests a book that has 2 authors.
     *
     * @return {@link Book} containing the expected values.
     */
    private static Book createRuralSettingsBookTest() {

        var book = new Book();
        book.setAuthor("안젤라 애커만");
        book.setAuthor2("베카 푸글리시");
        book.setTitle("디테일 사전 : 시골 편");
        book.setPublishDateFormatted("04/20/2021");
        book.setIsbn(9791155813614L);
        book.setPages(464);
        book.setPublisher("윌북");
        book.setBookSizeFormatted("15.2 x 22");
        book.setOriginalPriceNumber(22000);
        book.setImageURL("https://image.aladin.co.kr/product/26913/67/cover500/k322730602_1.jpg");
        book.setTranslator("최세희");
        book.setEnglishTitle("The Rural Setting Thesaurus: A Writer's Guide to Personal and Natural Places");
        return book;
    }
    /**
     * Gets the expected values for the test of the book called '소설 보다 : 봄 2021'
     * Tests a book that has more than 2 authors.
     *
     * @return {@link Book} containing the expected values.
     */
    private static Book createSocialBookTest() {

        var book = new Book();
        book.setAuthor("김멜라");
        book.setAuthor2("1494");
        book.setTitle("소설 보다 : 봄 2021");
        book.setPublishDateFormatted("03/16/2021");
        book.setIsbn(9788932038339L);
        book.setPages(146);
        book.setPublisher("문학과지성사");
        book.setBookSizeFormatted("11.4 x 18.8");
        book.setOriginalPriceNumber(3500);
        book.setImageURL("https://image.aladin.co.kr/product/26725/73/cover500/8932038333_1.jpg");
        book.setTranslator("");
        book.setEnglishTitle("");
        return book;
    }


    /**
     * Creates a {@link Document} for testing.
     * The file should be located in the same package as the test class.
     *
     * @param fileName the name of the file to test.
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private static Document createDocument(String fileName) throws URISyntaxException, IOException {
        var url = AladinBookCreatorTest.class.getResource(fileName);
        if(url == null){
            throw new IllegalArgumentException(
                String.format("File doesn't exist or path is wrong: %s", fileName));
        }
        File f = new File(url.toURI());
        return Jsoup.parse(f, "UTF-8");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bookCreator = new AladinBookCreator(bookWindowService, oclcService);
    }

    @Test
    public void fillInBasicData() {
        var book = new Book();
        var result = bookCreator.fillInBasicData(book, doc);

        Assert.assertEquals(expected.getIsbn(), book.getIsbn());
        Assert.assertEquals(expected.getAuthor(), book.getAuthor());
        Assert.assertEquals(expected.getTitle(), book.getTitle());
        Assert.assertEquals(expected.getPages(), book.getPages());
        Assert.assertEquals(expected.getPublisher(), book.getPublisher());
        Assert.assertEquals(expected.getBookSizeFormatted(), book.getBookSizeFormatted());
        Assert.assertEquals(expected.getOriginalPriceNumber(), book.getOriginalPriceNumber());
        Assert.assertEquals(expected.getImageURL(), book.getImageURL());
        Assert.assertEquals(expected.getPublishDateFormatted(), book.getPublishDateFormatted());
        Assert.assertEquals(expected.getAuthor2(), book.getAuthor2());
        Assert.assertEquals(expected.getTranslator(), book.getTranslator());
        Assert.assertEquals(expected.getType(), book.getType());
        Assert.assertEquals(expected.getEnglishTitle(), book.getEnglishTitle());
    }
}