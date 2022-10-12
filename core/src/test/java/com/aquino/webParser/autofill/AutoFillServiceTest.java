package com.aquino.webParser.autofill;

import com.aquino.webParser.ProcessorFactoryImpl;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

/**
 * Tests the romanization of the korean authors' names.
 */
@RunWith(Parameterized.class)
public class AutoFillServiceTest {

    private final String koreanName, englishLast, englishFirst;
    private AutoFillService autoFillService;

    public AutoFillServiceTest(String koreanName, String englishLast, String englishFirst) {
        this.koreanName = koreanName;
        this.englishLast = englishLast;
        this.englishFirst = englishFirst;
    }

    @Parameterized.Parameters(name = "{index}: {0}-{1}")
    public static Iterable<Object[]> authorData() throws IOException, URISyntaxException {
        return Arrays.asList(
            new Object[][]{
                {"이장무", "Lee", "Jang Mu"},
                {"김정은", "Kim", "Jeong Eun"},
                {"박근혜", "Park", "Geun Hye"},
            }
        );
    }

    @Before
    public void setUp() throws Exception {
        var factory = new ProcessorFactoryImpl();

        this.autoFillService = new AutoFillServiceImpl(null, null, null,
            Map.of(Language.Korean, new KoreanAuthorStrategy(factory.GetKoreanLastNames())));
        this.autoFillService.setLanguage(Language.Korean);
    }

    @Test
    public void createAuthor() {
        var book = new Book();
        book.setAuthor(this.koreanName);
        var a = this.autoFillService.CreateAuthor(book.getAuthor());

        Assert.assertEquals(this.englishLast, a.getEnglishFirstName());
        Assert.assertEquals(this.englishFirst, a.getEnglishLastName());
    }
}