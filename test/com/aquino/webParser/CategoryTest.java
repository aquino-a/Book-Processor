/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import com.sun.istack.internal.logging.Logger;
import java.util.logging.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alex
 */
public class CategoryTest {
    
        private static final Logger logger = Logger.getLogger(CategoryTest.class);
        
        Book book1 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686");
        Book book2 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz");
        Book book3 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880");
        Book book4 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123204587");
        Book book5 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823");
        Book book6 = new Book("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=135519424");
        Book book7 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=122235693");
    
    public CategoryTest() {
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
        
        assertEquals("에세이", book1.getCategory());
        assertEquals("자기계발", book2.getCategory());
        assertEquals("유아", book3.getCategory());
        assertEquals("소설/시/희곡", book4.getCategory());
        assertEquals("자기계발", book5.getCategory());
        assertEquals("사회과학", book6.getCategory());
        assertEquals("소설/시/희곡", book7.getCategory());
        
        
        
    }
    
}
