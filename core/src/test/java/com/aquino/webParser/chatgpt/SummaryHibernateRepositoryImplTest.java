/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import java.io.IOException;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Test;

import com.aquino.webParser.model.SavedBook;

import junit.framework.TestCase;

/**
 *
 * @author alex
 */
public class SummaryHibernateRepositoryImplTest extends TestCase {

    private HibernateSummaryRepository sr;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final var registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();

        var metadataSources = new MetadataSources(registry);
        metadataSources.addAnnotatedClass(SavedBook.class);

        var sessionFactory = metadataSources
                .buildMetadata()
                .buildSessionFactory();

        sr = new HibernateSummaryRepository(sessionFactory);
    }

    @Test
    public void testSave() throws IOException {
        var isbn = "1";
        var summary = "hehe";
        sr.save(isbn, summary);
        var result = sr.get(isbn);

        assertEquals("result must match", summary, result);
    }

    @Test
    public void testUpdate() throws IOException {
        var isbn = "1";
        var summary = "hehe2";
        sr.save(isbn, summary);
        var result = sr.get(isbn);

        assertEquals("result must match", summary, result);
    }
    
    @Test
    public void testTitleSave() throws IOException {
        var isbn = "2";
        var title = "hehe";
        sr.saveTitle(isbn, title);
        var result = sr.getTitle(isbn);

        assertEquals("result must match", title, result);
    }

    @Test
    public void testCategorySave() throws IOException {
        var isbn = "3";
        var category = "hehe";
        sr.saveCategory(isbn, category);
        var result = sr.getCategory(isbn);

        assertEquals("result must match", category, result);
    }
}
