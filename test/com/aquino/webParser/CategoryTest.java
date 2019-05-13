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
 * @author alex
 */
public class CategoryTest {
    
        private static final Logger logger = Logger.getLogger(CategoryTest.class.getName());
        
        OldBook oldBook1 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686");
        OldBook oldBook2 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz");
        OldBook oldBook3 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880");
        OldBook oldBook4 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123204587");
        OldBook oldBook5 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823");
        OldBook oldBook6 = new OldBook("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=135519424");
        OldBook oldBook7 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=122235693");
    
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
        
        assertEquals("에세이", oldBook1.getCategory());
        assertEquals("자기계발", oldBook2.getCategory());
        assertEquals("유아", oldBook3.getCategory());
        assertEquals("소설/시/희곡", oldBook4.getCategory());
        assertEquals("자기계발", oldBook5.getCategory());
        assertEquals("사회과학", oldBook6.getCategory());
        assertEquals("소설/시/희곡", oldBook7.getCategory());
        
        
        
    }
    
}
