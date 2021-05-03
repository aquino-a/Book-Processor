package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AutoFillServiceTest {

    @Test
    public void createAuthor() {
        var afs = new AutoFillService(null, null, null);
        afs.setLanguage(Language.Korean);
        afs.setKoreanLastNames(Map.of("이","Lee"));

        var b = new Book();
        b.setAuthor("이장무");
        var a = afs.CreateAuthor(b);

        Assert.assertEquals("Lee", a.getEnglishFirstName());
        Assert.assertEquals("Jang Mu", a.getEnglishLastName());

    }
}