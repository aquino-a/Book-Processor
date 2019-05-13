/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import java.util.logging.Level;
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
public class DescriptionScrapingTest {
    
    private static final Logger logger = Logger.getLogger(DescriptionScrapingTest.class.getName());
    DescriptionWriter writer;
    
    OldBook oldBook1 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686");
    OldBook oldBook2 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz");
    OldBook oldBook3 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880");
    OldBook oldBook4 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123204587");
    OldBook oldBook5 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823");
    OldBook oldBook6 = new OldBook("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=135519424");
    OldBook oldBook7 = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=122235693");
    
    public DescriptionScrapingTest() {
        writer = new DescriptionWriter();
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

    /**
     * Test of writeBooks method, of class DescriptionWriter.
     */
    @Test
    public void testWriteBooks() {
        
        logger.log(Level.INFO, "Starting test {0}", oldBook1.getTitle());
        String des = oldBook1.getDescription();
        assertTrue(des.startsWith("'유병재 천재설' 의혹(?)마저"));
        assertTrue(des.endsWith(" 진수를 보여준다."));
        
        logger.log(Level.INFO, "Starting test {0}", oldBook2.getTitle());
        des = oldBook2.getDescription();
        assertTrue(des.startsWith("수많은 선택지와 기회비용 "));
        assertTrue(des.endsWith(" 변화시켜준다."));
        
        logger.log(Level.INFO, "Starting test {0}", oldBook3.getTitle());
        des = oldBook3.getDescription();
        assertTrue(des.startsWith("<구름 빵>에서 <알사탕>까지 개성 "));
        assertTrue(des.endsWith(" 구매할 수 있다."));
        
        logger.log(Level.INFO, "Starting test {0}", oldBook4.getTitle());
        des = oldBook4.getDescription();
        assertTrue(des.startsWith("『그』에게 『재도전』하기 "));
        assertTrue(des.endsWith("돌입하는 미궁담 12탄."));
        
        logger.log(Level.INFO, "Starting test {0}", oldBook5.getTitle());
        des = oldBook5.getDescription();
        assertTrue(des.startsWith("캘리포니아대학교 산하 ‘"));
        assertTrue(des.endsWith(" 6개월이기 때문이다."));
        
        logger.log(Level.INFO, "Starting test {0}", oldBook6.getTitle());
        des = oldBook6.getDescription();
        assertTrue(des.startsWith("자신의 모순을 인정하며, "));
        assertTrue(des.endsWith(" 선언문이다."));
        
        //doesn't work so is an empty string.
        logger.log(Level.INFO, "Starting test {0}", oldBook7.getTitle());
        des = oldBook7.getDescription();
        assertEquals(des, "");
        System.out.println(des);
        
    }
    
}
