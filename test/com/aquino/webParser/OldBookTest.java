/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
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
public class OldBookTest {
    
    
    private static final Logger logger = Logger.getLogger(OldBookTest.class.getName());
        
    OldBook oldBook1 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686");
    OldBook oldBook2 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz");
    OldBook oldBook3 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880");
    OldBook oldBook4 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123204587");
    OldBook oldBook5 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823");
    OldBook oldBook6 = new OldBook("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=135519424");
    OldBook oldBook7 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=122235693");
    
    public OldBookTest() {
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
    public void Book1() {
        assertEquals("", oldBook1.getAuthorOriginal());
        long start = System.currentTimeMillis();
        assertEquals(1077331600L, oldBook1.getOCLC());
        long stop = System.currentTimeMillis();
        System.out.printf("oclc fetch time: %d ms", stop-start);
//        assertEquals("Mark Manson", oldBook2.getAuthorOriginal());
//        assertEquals("", oldBook3.getAuthorOriginal());
//        assertEquals("大森藤ノ", oldBook4.getAuthorOriginal());
//        assertEquals("Intelligent Change", oldBook5.getAuthorOriginal());
//        assertEquals("Roxane Gay", oldBook6.getAuthorOriginal());
//        assertEquals("", oldBook7.getAuthorOriginal());
        
    }

    @Test
    public void JsonTest() throws IOException {
        Connection.Response r =  Jsoup.connect("http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=ttbiamqnibus1956001&itemIdType=ItemId&ItemId=182285146&output=js").method(Connection.Method.GET).execute();
        String s = r.body();
        ObjectMapper m = new ObjectMapper();
        JsonTestData d =  m.readValue(s, JsonTestData.class);
    }


}
