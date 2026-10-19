package com.aquino.webParser.bookCreators.yahoo;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.aquino.webParser.model.Book;

public class YahooBookCreatorTest {
    private YahooBookCreator bc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bc = new YahooBookCreator();
    }

    @Test
    public void testFillInDetailsLive() throws IOException {
        var book = new Book();
        // book.setBookPageUrl("https://shopping.yahoo.co.jp/search?p=9784759823134&cid=&brandid=&kspec=&b=1");
        book.setBookPageUrl("https://store.shopping.yahoo.co.jp/honyaclubbook/9784759823134.html");

        bc.fillInAllDetails(book);

        assertThat(book.getAuthorBooks(), is("マリアホ・イルストゥラホ / イルストゥラホ,マリアホ"));
        assertThat(book.getAuthor2Books(), is("鈴木沙織 / スズキ,サオリ"));
    }
}
