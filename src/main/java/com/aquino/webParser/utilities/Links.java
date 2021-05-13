/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.StringJoiner;

/**
 * @author alex
 */
public class Links {

    private static final Logger LOGGER = LogManager.getLogger();
    private static Type type = Type.NEW;

    public static Type getType() {
        return type;
    }

    public static void setType(Type type) {
        Links.type = type;
    }

    private static String buildOCLCURL(String pageNumber) {
        if (type == Type.NEW)
            return "http://www.aladin.co.kr/shop/common/wnew.aspx?ViewRowsCount=50&ViewType=Detail&SortOrder=6&page=" + pageNumber;
        if (type == Type.BEST)
            return "https://www.aladin.co.kr/shop/common/wbest.aspx?BestType=Bestseller&BranchType=1&CID=0&page=" + pageNumber;
        else
            return "http://www.aladin.co.kr/shop/common/wnew.aspx?ViewRowsCount=50&ViewType=Detail&SortOrder=6&page=" + pageNumber;
    }

    public static String getPageofLinks(int pageNumber) throws IOException {
        LOGGER.info("Starting page {0}", pageNumber);
        return pageOfLinks(getSearchElements(pageNumber));
    }

    private static String pageOfLinks(Elements elements) {
        StringJoiner joiner = new StringJoiner("\n");
//        for (Element element : elements) {
//            joiner.add(element.attr("href"));
//        }
        elements.stream().map(e -> e.attr("href"))
            .forEach(e -> joiner.add(e));
        LOGGER.info("Got the Links");
//        String links = joiner.toString();

        return joiner.toString();
    }

    private static Elements getSearchElements(int pageNumber) throws IOException {
        Document doc = Connect.connectToURL(buildOCLCURL(String.valueOf(pageNumber)));
        Elements elements = doc.getElementsByClass("bo3");
        if (elements.first() == null)
            throw new IOException("No book");
        else return elements;
    }

    public enum Type {
        BEST(20), NEW(47);
        private final int pages;

        Type(int pages) {
            this.pages = pages;
        }

        public int getPages() {
            return pages;
        }

    }
//    public String getPagesOfLinks(int pageAmount) {
//        StringJoiner joiner = new StringJoiner("\n");
//        for(int i = 0; i < pageAmount; i++) {
//            joiner.add(pageOfLinks(getSearchElements(i)));
//        }
//        return joiner.toString();
//    }

}
