/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author b005
 */
public class BookTest {
    
    
    private static final Logger logger = Logger.getLogger(BookTest.class.getName());
        
    Book book1 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686");
    Book book2 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz");
    Book book3 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880");
    Book book4 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123204587");
    Book book5 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823");
    Book book6 = new Book("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=135519424");
    Book book7 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=122235693");
    
    public BookTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        assertEquals("", book1.getAuthorOriginal());
        assertEquals("Mark Manson", book2.getAuthorOriginal());
        assertEquals("", book3.getAuthorOriginal());
        assertEquals("大森藤ノ", book4.getAuthorOriginal());
        assertEquals("Intelligent Change", book5.getAuthorOriginal());
        assertEquals("Roxane Gay", book6.getAuthorOriginal());
        assertEquals("", book7.getAuthorOriginal());
        
    }
    
}
