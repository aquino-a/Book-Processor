package com.aquino.webParser.Amazon;

import com.aquino.webParser.Book;
import com.aquino.webParser.Utilities.Connect;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmazonJapanBookCreator {


    private static final String searchUrlFormat = "https://www.amazon.co.jp/s?k=%s";
    private static final String kinoBookUrlFormat = "https://www.kinokuniya.co.jp/f/dsg-01-%s";
    private static final Logger logger = Logger.getLogger(AmazonJapanBookCreator.class.getName());
    private static final DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
    private static final DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public Book createBook(String isbn){
        Document doc = Connect.connectToURL(String.format(searchUrlFormat, isbn));
        return null;
    }


    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        Book book = new Book();
        Document doc = Connect.connectToURL(bookPageUrl);
        if(doc == null)
            throw new IOException(String.format("Document wasn't loaded: %s",bookPageUrl));
        return fillInBasicData(book, doc);

    }

    private Book fillInBasicData(Book book, Document doc) {
        book.setOriginalPriceNumber(parsePrice(doc));
        book.setTitle(parseTitle(doc));
        book = parseAuthorDetails(book, doc);
        book = parseSecondDetailSection(book, doc);
        return book;
    }

    private Book parseAuthorDetails(Book book, Document doc) {
        try{
            Elements es = doc.getElementById("bylineInfo").getElementsByClass("author notFaded");
            int authorCount = 0;
            String contributionType = null;
            for (Element e : es) {
                contributionType = e.getElementsByClass("contribution").first().ownText();
                if(contributionType.contains("翻訳"))
                    book.setTranslator(findContributorName(e));
                else if(contributionType.contains("著")){
                    if(authorCount == 0){
                        authorCount++;
                        book.setAuthor(findContributorName(e));
                    } else if (authorCount == 1){
                        authorCount++;
                        book.setAuthor(findContributorName(e));
                    } else if(authorCount > 1){
                        book.setAuthor("1494");
                        book.setAuthor2("");
                    }
                }
            }

        }catch (Exception e){
            logger.log(Level.WARNING, String.format("Couldn't parse author details: %s", e.getMessage()));
            e.printStackTrace();
//            book.setAuthor("");
//            book.setAuthor2("");
//            book.setTranslator("");
//            book.setEnglishTitle("");
        }
        finally {
            //TODO implement this;
//            setDefaultAutorSection(book);
            return book;
        }
    }

    private String findContributorName(Element e) {
        return e.getElementsByTag("a").first().ownText();
    }

    private int parsePrice(Document doc) {
        String priceSource = doc.getElementsByClass("a-size-base a-color-price a-color-price").first().text().trim();
        try {
            return NumberFormat.getInstance(Locale.JAPAN).parse(priceSource.replace("￥","").trim()).intValue();
        } catch (ParseException e) {
            return -1;
        }
    }

    private String parseTitle(Document doc) {
        String titleSource = doc.getElementById("productTitle").ownText().trim();
        if(titleSource.indexOf("(") > -1)
            return titleSource.substring(0, titleSource.indexOf("("));
        else return titleSource;
    }

    private Book parseSecondDetailSection(Book book, Document doc) {
        Elements es =  doc.getElementById("detail_bullets_id").getElementsByTag("ul").first().getElementsByTag("li");
        for (Element e : es ){
            String whole = e.wholeText();
            if(whole.contains("ページ"))
                book.setPages(findPages(e.ownText()));
            else if(whole.contains("出版社"))
                book.setPublisher(findPublisher(e.ownText().trim()));
            else if(whole.contains("発売日"))
                book.setPublishDateFormatted(findPublishedDateFormatted(e.ownText().trim()));
            else if(whole.contains("梱包サイズ"))
                book.setBookSizeFormatted(findBookSizeFormatted(e.ownText().trim()));
        }
        return book;
    }
    private int findPages(String pagesSource) {
        try {
            return Integer.parseInt(pagesSource.substring(0,pagesSource.indexOf("ページ")));
        } catch (NumberFormatException e){
            return -1;
        }
    }

    private String findPublisher(String publisherSource) {
        if(publisherSource.contains(";"))
            return publisherSource.substring(0, publisherSource.indexOf(";")).trim();
        else if(publisherSource.contains("("))
            return publisherSource.substring(0,publisherSource.indexOf("(")).trim();
        logger.log(Level.WARNING, String.format("Couldn't find publisher: %s", publisherSource));
        return "";
    }

    private String findPublishedDateFormatted(String dateSource) {
        //MM/DD/YYYY
        try {
            LocalDate date = LocalDate.parse(dateSource, sourceFormatter);
            return date.format(targetFormatter);
        } catch (DateTimeParseException e){
            logger.log(Level.WARNING, String.format("Couldn't find publish date: %s", dateSource));
            return "";
        }
    }

    private String findBookSizeFormatted(String sizeSource) {
        //28.5 x 20.9 x 1.4 cm
        if(sizeSource.lastIndexOf("x") > -1)
            return sizeSource.substring(0, sizeSource.lastIndexOf("x")).trim();
        logger.log(Level.WARNING, String.format("Couldn't find book size: %s", sizeSource));
        return "";
    }


}
