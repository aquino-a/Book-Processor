/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import java.io.File;
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
public class DescriptionWriterTest {
    
    private static final Logger logger = Logger.getLogger(DescriptionWriterTest.class.getName());
    DescriptionWriter writer;
    
    Book book1 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686");
    Book book2 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz");
    Book book3 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880");
    Book book4 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123204587");
    Book book5 = new Book("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823");
    
    public DescriptionWriterTest() {
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
        
        File file = new File("text.txt");
        String des = book1.getDescription();
        writer.writeBooks(new Book[]{book1});
        writer.saveBooks(file);
        logger.log(Level.INFO, "Starting test {0}", book1.getTitle());
        assertEquals("'유병재 천재설' 의혹(?)마저 불러일으킨 전 국민의 웃음 폭탄 유병재의 첫 에세이. 코미디언이자 작가인 유병재가 지난 3년 동안 저축하듯 모은 에세이, 우화, 아이디어 노트, 그리고 미공개 글을 총 4장 200여 페이지에 담았다. 작가가 스스로 작명한 '농담집'이라는 제목부터 호기심을 자아낸다.\n" +
"\n" +
"누구나 말하고 싶지만 말할 수 없는 분노와 모순 가득한 세상 속에서 역시 모순으로부터 자유로울 수 없는 작가의 모습이 진지하면서도 재치 넘치게 그려졌다. 흔히 말장난을 의미하는 '농담'이라는 개념이 유병재의 펜 끝에서 폭소와 비판, 공감과 풍자를 오가며 '즐거움이라는 한 가지 감정에만 의존하지 않는' 진짜 블랙코미디의 진수를 보여준다.", des);
        
        
        
    }
    
}
