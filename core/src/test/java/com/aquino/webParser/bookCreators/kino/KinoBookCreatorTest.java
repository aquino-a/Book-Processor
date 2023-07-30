package com.aquino.webParser.bookCreators.kino;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.honto.HontoBookCreator;
import com.aquino.webParser.bookCreators.honya.HonyaClubBookCreator;
import com.aquino.webParser.chatgpt.ChatGptService;
import com.aquino.webParser.oclc.OclcService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class KinoBookCreatorTest {

    private KinoBookCreator bc;
    @Mock
    BookWindowService bookWindowService;
    @Mock
    OclcService oclcService;
    @Mock
    ChatGptService chatGptService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bc = new KinoBookCreator(
            bookWindowService, 
            chatGptService,
            new HontoBookCreator(),
            new HonyaClubBookCreator());
    }

    @Test
    public void testBookListFromIsbnLive() throws IOException {
        var book = bc.createBookFromIsbn("9784022518965");
        
        assertThat(book.getIsbn(), is(9784022518965L));
        assertThat(book.getTitle(), is("ふるさとに風が吹く―福島からの発信と地域ブランディングの明日"));
        assertThat(book.getPages(), is(272));
        assertThat(book.getPublisher(), is("朝日新聞出版"));
        assertThat(book.getAuthor(), is("箭内 道彦"));
        assertThat(book.getAuthor2(), is("河尻 亨一"));
        assertThat(book.getImageURL(), is("https://www.kinokuniya.co.jp/images/goods/ar2/web/imgdata2/large/40225/4022518960.jpg"));
    }
}
