/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.Utilities;

import java.io.IOException;
import java.util.StringJoiner;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author alex
 */
public class Links {
    private static String buildOCLCURL(String pageNumber) {
        return "http://www.aladin.co.kr/shop/common/wnew.aspx?ViewRowsCount=50&ViewType=Detail&SortOrder=6&page=" + pageNumber;
    }
    public static String getPageofLinks(int pageNumber) throws IOException {
        System.out.println(pageNumber);
        return pageOfLinks(getSearchElements(pageNumber));
    }
    private static String pageOfLinks(Elements elements) {
        StringJoiner joiner = new StringJoiner("\n");
//        for (Element element : elements) {
//            joiner.add(element.attr("href"));
//        }
        elements.stream().map(e -> e.attr("href"))       
           .forEach(e-> joiner.add(e));
        System.out.println("got a page");
//        String links = joiner.toString();
        
        return joiner.toString();
    }
    private static Elements getSearchElements(int pageNumber) throws IOException {
        Document doc = Connect.connectToURL(buildOCLCURL(String.valueOf(pageNumber)));
        Elements elements =  doc.getElementsByClass("bo3");
        if(elements.first() == null)
            throw new IOException("No book");
        else return elements;
    }
//    public String getPagesOfLinks(int pageAmount) {
//        StringJoiner joiner = new StringJoiner("\n");
//        for(int i = 0; i < pageAmount; i++) {
//            joiner.add(pageOfLinks(getSearchElements(i)));
//        }
//        return joiner.toString();
//    }
    
}
