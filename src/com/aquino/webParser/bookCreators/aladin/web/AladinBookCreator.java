package com.aquino.webParser.bookCreators.aladin.web;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

public class AladinBookCreator implements BookCreator {

    private static final Pattern translatorPattern = Pattern.compile("([\\u3131-\\uD79D]{3}) \\(옮긴이\\)");
    private static final Pattern originalTitlePattern = Pattern.compile("원제 : ([\\p{L}:'\\*,0-9’\\- ]+)(?: \\( \\d{4}년\\))?");
    private static final Pattern publisherPattern = Pattern.compile("\\)([\\u3131-\\uD79DA-Za-z ]+)(?:\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern publishDatePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern authorsPattern = Pattern.compile("((?:[\\u3131-\\uD79D]+,)*)([\\u3131-\\uD79D ]+) \\(지은이\\)");
    private static final Pattern oclcPattern = Pattern.compile("oclc/(\\d+)$");

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        var book = new Book();
        var doc = Connect.connectToURL(bookPageUrl);
        if(doc == null)
            throw new IOException(String.format("Document wasn't loaded: %s",bookPageUrl));
        book.setBookPageUrl(bookPageUrl);
        return fillInBasicData(book, doc);
    }

    private int parsePrice(Document doc) {
        var originalPrice = doc.getElementsByClass("info_list").first().getElementsByClass("Ritem").first().text();
        var sb = new StringBuilder(originalPrice);
        sb.deleteCharAt(sb.length() - 1);
        try {
            return NumberFormat.getInstance(Locale.KOREA).parse(sb.toString()).intValue();
        } catch (ParseException e) {
            return -1;
        }
    }

    private String parseTitle(Document doc) {
        return  doc.getElementsByAttributeValueMatching("name", "twitter:title").attr("content");
    }

    private String parseDescription(Book book) {
        var map = new HashMap<String, String>();
        map.put("Referer", book.getLocationUrl());
        Document doc = null;
        if (book.getkIsbn() != null) {
            doc = Connect.connectToURLwithHeaders(
                    createLazyDescriptionUrl(String.valueOf(book.getkIsbn())), map);
        } else {
            doc = Connect.connectToURLwithHeaders(
                    createLazyDescriptionUrl(book.getIsbnString()), map);
        }
        return findDescription(doc);
    }

    private static String findDescription(Document doc) {
        String result = null;
        for (Element element : doc.getAllElements()) {
            for (Node n : element.childNodes()) {
                if (n.nodeName().equals("#comment")) {
                    if ("\n<!-- 책소개-->".contentEquals(n.toString())) {
                        try {
                            result = Jsoup.parse(
                                    element.siblingElements().toString())
                                    .getElementsByClass("Ere_prod_mconts_R")
                                    .first().wholeText().trim();
                        } catch (NullPointerException e) {
                            result = "";
                        }
                    }
                }
            }
        }
        return result;
    }

    private String createLazyDescriptionUrl(String details) {
        return "https://www.aladin.co.kr/shop/product/getContents.aspx?ISBN="
                + details + "&name=Introduce&type=0&date=11";
    }

    private String parseImageUrl(Document doc) {
        var img = doc.getElementsByAttributeValueMatching(
                "property", "og:image").attr("content");
        try {
            imageCheck(img);
            return img;
        } catch (Exception e) {
            return "Problem with image";
        }
    }
    private void imageCheck(String url) throws IOException {
        ImageIO.read(new URL(url));
    }

    private Book fillInBasicData(Book book, Document doc) {
        book.setOriginalPriceNumber(parsePrice(doc));
        book.setTitle(parseTitle(doc));
        book.setDescription(parseDescription(book));
        book.setCover(parseType(doc));
        book.setImageURL(parseImageUrl(doc));
        book = parseAuthorDetails(book, doc);
        book = parseSecondDetailSection(book, doc);
        book = setWeight(book);
        book.setLanguageCode("KOR");
        book.setCurrencyType("Won");

        return book;
    }

    private Book parseAuthorDetails(Book book, Document doc) {
        String authorSection = doc.getElementsByClass("Ere_sub2_title").first().text();

        book.setTranslator(FindTranslator(authorSection));
        book.setEnglishTitle(FindOriginalTitle(authorSection));
        book.setPublisher(FindPublisher(authorSection));
        book.setPublishDateFormatted(formatDate(FindPublishDate(authorSection)));
        SetAuthors(this, authorSection);

        if(author != "1494")
            retrieveAuthorNumber();
        retrievePublisherNumber();
        if (author2 != null && !author2.equals("")) {
            retrieveAuthor2Number();
        }
    }

    private String formatDate(String date) {
        StringTokenizer st = new StringTokenizer(date, "-");
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append(st.nextToken());
        sb.insert(0, st.nextToken());
        sb.insert(2, st.nextToken());
        sb.insert(2, '/');
        return sb.toString();
    }

    private Book parseSecondDetailSection(Book book, Document doc) {
        String bookDetailSection = doc.getElementsByClass("conts_info_list1").first().text();
        var stringTokenizer = new StringTokenizer(bookDetailSection," ");
        while(stringTokenizer.hasMoreTokens()){
            String s = stringTokenizer.nextToken();
            if(s.contains("반양장본"))
                book.setCover("PB");
            else if(s.contains("양장본"))
                book.setCover("HC");
            else if(s.contains("쪽"))
                book.setPages(Integer.parseInt(s.substring(0,s.length()-1)));
            else if(s.contains("m") || s.contains("*"))
                book.setBookSize(formatBookSize(s));
        }
        return book;
    }

    private String formatBookSize(String size) {
        StringTokenizer st = new StringTokenizer(size, "*m");
        try {
            double first = Double.parseDouble(st.nextToken()) / 10;
            double second = Double.parseDouble(st.nextToken()) / 10;
            if (first % 1 != 0 && second % 1 != 0) {
                return first + " x " + second;
            } else if (first % 1 == 0 && second % 1 == 0) {
                return (int) first + " x " + (int) second;
            } else if (first % 1 == 0) {
                return (int) first + " x " + second;
            } else if (second % 1 == 0) {
                return first + " x " + (int) second;
            } else return size;
        } catch (NumberFormatException | NoSuchElementException e) {
            return "problem";
        }
    }

    private Book setWeight(Book book) {
        var pages = book.getPages();
        book.setWeight(pages % 300 > 1 ? (pages / 300) + 1 : pages / 300);
        return book;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        return null;
    }

    @Override
    public String BookPagePrefix() {
        return null;
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        return null;
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        return null;
    }

    @Override
    public void checkInventoryAndOclc(Book result) {

    }
}
