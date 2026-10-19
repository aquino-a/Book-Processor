package com.aquino.webParser.bookCreators.yahoo;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.isNull;
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
        book.setBookPageUrl("https://store.shopping.yahoo.co.jp/honyaclubbook/9784759823134.html");

        bc.fillInAllDetails(book);

        assertThat(book.getAuthorBooks(), is("マリアホ・イルストゥラホ / イルストゥラホ,マリアホ"));
        assertThat(book.getAuthor2Books(), is("鈴木沙織 / スズキ,サオリ"));
    }
    
    @Test
    public void testFillInDetailsLive2() throws IOException {
        var book = new Book();
        book.setBookPageUrl("https://store.shopping.yahoo.co.jp/honyaclubbook/9784865373295.html");

        bc.fillInAllDetails(book);

        assertThat("Author 1", book.getAuthorBooks(), is("鉄人ノンフィクション編集部 / テツジン ノンフィクション ヘンシュウブ"));
        assertNull("Author 2", book.getAuthor2Books());
    }

}
