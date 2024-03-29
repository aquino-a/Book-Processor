/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alex
 */
public class SummaryRepositoryImplTest {

    @Test
    public void testSave() throws IOException {
        var path = "./test";
        var sr = new SummaryRepositoryImpl(path);

        var isbn = "1234";
        var summary = "hehe";
        sr.save(isbn, summary);
        var result = sr.get(isbn);

        assertEquals("result must match", summary, result);
    }

    @Test
    public void testTitleSave() throws IOException {
        var path = "./test";
        var sr = new SummaryRepositoryImpl(path);

        var isbn = "1234";
        var title = "hehe";
        sr.saveTitle(isbn, title);
        var result = sr.getTitle(isbn);

        assertEquals("result must match", title, result);
    }

    @Test
    public void testCategorySave() throws IOException {
        var path = "./test";
        var sr = new SummaryRepositoryImpl(path);

        var isbn = "1234";
        var category = "hehe";
        sr.saveCategory(isbn, category);
        var result = sr.getCategory(isbn);

        assertEquals("result must match", category, result);
    }
}
