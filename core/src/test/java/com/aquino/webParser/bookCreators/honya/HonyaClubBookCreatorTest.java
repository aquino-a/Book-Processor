package com.aquino.webParser.bookCreators.honya;

import com.aquino.webParser.bookCreators.BasicBookCreatorTest;
import com.aquino.webParser.bookCreators.DocumentCreator;
import com.aquino.webParser.model.Book;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@RunWith(Parameterized.class)
public class HonyaClubBookCreatorTest extends BasicBookCreatorTest {

    private static final DocumentCreator DOCUMENT_CREATOR = new DocumentCreator(HonyaClubBookCreatorTest.class);
    private HonyaClubBookCreator bookCreator;

    public HonyaClubBookCreatorTest(Document doc, Book expected) {
        super(doc, expected);
    }

    @Before
    public void setUp() throws Exception {
        bookCreator = new HonyaClubBookCreator();
    }

    @Parameterized.Parameters(name = "{index}: {1}")
    public static Iterable<Object[]> bookData() throws IOException, URISyntaxException {
        return Arrays.asList(
            new Object[][]{
                {DOCUMENT_CREATOR.createDocument("first.html"), createFirstBookTest() },
            }
        );
    }

    /**
     * Gets the first book values to test against.
     *
     * @return the first book to test.
     */
    private static Book createFirstBookTest() {
        var book = new Book();
        book.setOriginalPriceNumber(1760);
        book.setDescription("５２ヘルツのクジラとは―他の鯨が聞き取れない高い周波数で鳴く");
        return book;
    }

    @Test
    public void fillInBasicData() {
        var book = new Book();
        var result = bookCreator.fillInBasicData(book, doc);

        Assert.assertTrue(book.getDescription().contains(expected.getDescription()));
        Assert.assertEquals(expected.getOriginalPriceNumber(), book.getOriginalPriceNumber());
    }
}