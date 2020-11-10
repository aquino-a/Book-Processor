package com.aquino.webParser;

import com.aquino.webParser.model.Author;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookWindowServiceImplTest {

    @Test
    public void addAuthor() {
        var bws = new BookWindowServiceImpl();
        var author = new Author();
        author.setEnglishFirstName("test111111");
        author.setEnglishLastName("test111111");
        var result = bws.addAuthor(author);
    }

    @Test
    public void RegexTest(){
        var text = "//    <html>\n" +
                "// <head></head>\n" +
                "//    <body>\n" +
                "//    {\"status\":\"success\",\"message\":\"Successfully Added Author # 33525\",\"redirect_url\":\"\\/admin\\/author\\/33525\\/edit\\/main\"}\n" +
                "// </body>\n" +
                "//</html>";
        var m = BookWindowServiceImpl.AUTHOR_NUMBER_REGEX.matcher(text);
        if(!m.find())
            Assert.fail();


    }
}