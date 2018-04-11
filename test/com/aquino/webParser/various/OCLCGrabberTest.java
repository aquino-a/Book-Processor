/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.various;

import com.aquino.webParser.Utilities.Connect;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 *
 * @author alex
 */
public class OCLCGrabberTest {
    private static String buildOCLCURL(String pageNumber) {
        return "http://www.aladin.co.kr/shop/common/wnew.aspx?ViewRowsCount=50&ViewType=Detail&SortOrder=6&page=" + pageNumber;
    }
    public void grabTest() throws IOException {
        ArrayList<Elements> elementList = new ArrayList<>();
        StringJoiner joiner = new StringJoiner("\n");
        IntStream.range(0, 2)
                .forEach(i -> elementList.add(getSearchElements(i)));
        elementList.stream().forEach(e -> {
            joiner.add(pageOfLinks(e));
        });
        System.out.println(joiner.toString());
        
//        Document doc = Jsoup.connect(buildOCLCURL("1")).get();
//        Elements el = doc.getElementsByClass("bo3");
//        StringJoiner joiner = new StringJoiner("\n");
//        el.stream().map(e -> e.attr("href"))       
//           .forEach(e-> joiner.add(e));
//        System.out.println(joiner.toString());
    }
    private static String pageOfLinks(Elements elements) {
        StringJoiner joiner = new StringJoiner("\n");
        elements.stream().map(e -> e.attr("href"))       
           .forEach(e-> joiner.add(e));
        return joiner.toString();
    }
    private static Elements getSearchElements(int pageNumber) {
        Document doc = Connect.connectToURL(buildOCLCURL(String.valueOf(pageNumber)));
//        Document doc = Jsoup.connect(buildOCLCURL(pageNumber)).get();
        return doc.getElementsByClass("bo3");
    }
    public static String getPagesOfLinks(int pageAmount) {
//        ArrayList<Elements> elementList = new ArrayList<>();
        StringJoiner joiner = new StringJoiner("\n");
//        IntStream.range(0, pageAmount).forEach(i -> {
//            joiner.add(pageOfLinks(getSearchElements(i)));
//        });
//        IntStream.range(0, pageAmount)
//                .forEach(i -> elementList.add(getSearchElements(i)));
//        elementList.stream().forEach(e -> {
//            joiner.add(pageOfLinks(e));
//        });
//
        for(int i = 0; i < pageAmount; i++) {
            joiner.add(pageOfLinks(getSearchElements(i)));
        }
        return joiner.toString();
    }
//    
//    public static void main(String[] args) {
//        System.out.println(getPagesOfLinks(5));
//    }
//    
//    
    
}
