/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import com.aquino.webParser.Utilities.Connect;
import com.aquino.webParser.Utilities.Login;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 *
 * @author alex
 */
public class Book {

    private Document doc;
    private String title, publishDate, originalPrice, bookSize,
            cover, publishDateFormatted, bookSizeFormatted,
            imageURL, author, englishTitle, translator,
            publisher, author2, isbnString, description, category, authorOriginal,
            locationUrl, kIsbn;
    private long isbn, oclc;

    private int pages, weight;
    private double originalPriceFormatted;
    
    private static final Logger logger = Logger.getLogger(Book.class.getName());

    private static final Pattern translatorPattern = Pattern.compile("([\\u3131-\\uD79D]{3}) \\(옮긴이\\)");
    private static final Pattern originalTitlePattern = Pattern.compile("원제 : ([\\p{L} ]+)(?: \\( \\d{4}년\\))?");
    private static final Pattern publisherPattern = Pattern.compile("\\)([\\u3131-\\uD79DA-Za-z ]+)(?:\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern publishDatePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern authorsPattern = Pattern.compile("((?:[\\u3131-\\uD79D]+,)*)([\\u3131-\\uD79D ]+) \\(지은이\\)");


    public Book(String url) {
//            System.out.println("in book constructor");
        doc = Connect.connectToURL(url);
        locationUrl = doc.location();
    }

    public Document getDoc() {
        return doc;
    }

    private void retrieveTitle() throws IllegalStateException {
        if (doc == null) {
            throw new IllegalStateException("There is no book./n"
                    + "Retry with proper url");
        }
        title = doc.getElementsByAttributeValueMatching("name", "twitter:title").attr("content");
    }

    public String getTitle() {
        if (title == null) {
            retrieveTitle();
        }
        return title;
    }

    private void retrieveISBN() {
        try {
            isbnString = doc.getElementsByAttributeValueMatching("property", "og:barcode").attr("content");
            isbn = Long.parseLong(isbnString);
        } catch (NumberFormatException e) {
            isbn = -1;
            isbnString = doc.getElementsByAttributeValueMatching("property", "books:isbn").attr("content");
        }
    }

    public Long getISBN() {
        if (isbn == 0) {
            retrieveISBN();
        }
        return isbn;
    }

    public String getIsbnString() {
        return isbnString;
    }

    private void retrieveOriginalPrice() {

//        originalPrice = doc.getElementsByClass("p_goodstd02").first().text();
        originalPrice = doc.getElementsByClass("info_list").first().getElementsByClass("Ritem").first().text();
        StringBuilder sb = new StringBuilder(originalPrice);
        sb.deleteCharAt(sb.length() - 1);
        originalPrice = sb.toString();
    }

    public String getOriginalPrice() {
        if (originalPrice == null) {
            retrieveOriginalPrice();
        }
        return originalPrice;
    }

    public int getOriginalPriceNumber() {
        try {
            return NumberFormat.getInstance(Locale.KOREA).parse(getOriginalPrice()).intValue();
        } catch (ParseException e) {
            return -1;
        }
    }

    private void retrieveDate() {
        Matcher m = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})").matcher(
                doc.getElementsByAttributeValueMatching(
                        "style", "height:25px; padding-right:10px;").text());
        if (m.find()) {
            publishDate = m.group();
        } else {
            System.out.println("No date found");
        }
    }

    public String getPublishDate() {
        if (publishDate == null) {
            retrieveDate();
        }
        return publishDate;
    }

    private void parseSecondDetailSection() {
        String bookDetailSection = doc.getElementsByClass("conts_info_list1").first().text();
        StringTokenizer stringTokenizer = new StringTokenizer(bookDetailSection," ");
        while(stringTokenizer.hasMoreTokens()){
            String s = stringTokenizer.nextToken();
            if(s.contains("반양장본"))
                cover = "PB";
            else if(s.contains("양장본"))
                cover = "HC";
            else if(s.contains("쪽"))
                pages = Integer.parseInt(s.substring(0,s.length()-1));
            else if(s.contains("mm"))
                bookSize = s;
        }
    }

    public String getType() {
        if (cover == null) {
            parseSecondDetailSection();
        }
        return cover;
    }

    public int getPages() {
        if (pages == 0) {
            parseSecondDetailSection();
        }
        return pages;
    }

    public String getBookSize() {
        if (bookSize == null) {
            parseSecondDetailSection();
        }
        return bookSize;
    }

    private void formatSize() {
        if (bookSize == null) {
            parseSecondDetailSection();
        }
        StringTokenizer st = new StringTokenizer(bookSize, "*m");
        try {
            double first = Double.parseDouble(st.nextToken()) / 10;
            double second = Double.parseDouble(st.nextToken()) / 10;
            if (first % 1 != 0 && second % 1 != 0) {
                bookSizeFormatted = first + " x " + second;
            } else if (first % 1 == 0 && second % 1 == 0) {
                bookSizeFormatted = (int) first + " x " + (int) second;
            } else if (first % 1 == 0) {
                bookSizeFormatted = (int) first + " x " + second;
            } else if (second % 1 == 0) {
                bookSizeFormatted = first + " x " + (int) second;
            }
        } catch (NumberFormatException e) {
            bookSizeFormatted = "problem";
        }
    }

    public String getBookSizeFormatted() {
        if (bookSizeFormatted == null) {
            formatSize();
        }
        if (bookSizeFormatted.equals("problem")) {
            return bookSize;
        }
        return bookSizeFormatted;
    }

    private void formatDate() {
        if (publishDate == null) {
            retrieveDate();
        }
        StringTokenizer st = new StringTokenizer(publishDate, "-");
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append(st.nextToken());
        sb.insert(0, st.nextToken());
        sb.insert(2, st.nextToken());
        sb.insert(2, '/');
        publishDateFormatted = sb.toString();
    }

    public String getPublishDateFortmatted() {
        if (publishDateFormatted == null) {
            formatDate();
        }
        return publishDateFormatted;
    }

    private String imageUrlCreate(String startUrl) {
        StringBuilder sb = new StringBuilder(startUrl);
        sb.replace(sb.indexOf("cover"), sb.indexOf("cover") + 5, "letslook");
        sb.setCharAt(sb.lastIndexOf("_") + 1, 'f');

//        sb.setCharAt(sb.lastIndexOf("1"), 'f');
//        sb.delete(sb.indexOf("cover"), sb.length());
//        sb.append("letslook/");
//        sb.append(doc.getElementsByAttributeValueMatching(
//                "property", "books:isbn").attr("content"));
//        sb.append("_f.jpg");
        return sb.toString();
    }

    private void retrieveImageUrl() {
        imageURL = imageUrlCreate(doc.getElementsByAttributeValueMatching(
                "property", "og:image").attr("content"));
        imageCheck();
    }

    public String getImageUrl() {
        if (imageURL == null) {
            retrieveImageUrl();
        }
        return imageURL;
    }

    private void imageCheck() {
        try {
            ImageIO.read(new URL(getImageUrl()));
        } catch (IOException e) {
            imageURL = "Problem with photo";
        }
    }

    private void formatOriginalPrice() {
//        StringBuilder sb = new StringBuilder(getOriginalPrice());
//        sb.deleteCharAt(sb.length()-1);
        try {
            originalPriceFormatted = NumberFormat.
                    getNumberInstance(Locale.KOREA).parse(getOriginalPrice()).doubleValue() / 1000;
        } catch (ParseException e) {
        }
    }

    public double getOriginalPriceFormatted() {
        if (originalPriceFormatted == 0) {
            formatOriginalPrice();
        }
        return originalPriceFormatted;
    }

    //change to string 12/18/17
    public static Book[] retrieveBookArray(String text) {
        StringTokenizer st = new StringTokenizer(text);
//        StringTokenizer st = new StringTokenizer(textArea.getText());
        ArrayList<Book> bookList = new ArrayList<>();
        while (st.hasMoreTokens()) {
            bookList.add(new Book(st.nextToken().trim()));
        }
        return bookList.toArray(new Book[bookList.size()]);
    }

    private void retrieveWeight() {
        if (pages == 0) {
            parseSecondDetailSection();
        }
        weight = pages % 300 > 1 ? (pages / 300) + 1 : pages / 300;
    }

    public int getWeight() {
        if (weight == 0) {
            retrieveWeight();
        }
        return weight;
    }

    private void retrieveOCLC() {
        Document oclcDoc;
        try {
            if (getISBN() != -1) {
                oclcDoc = Connect.connectToURL("http://www.worldcat.org/search?q=" + getISBN());
                oclc = Long.parseLong(oclcDoc.getElementsByClass("oclc_number").first().text());
            } else {
                oclc = -1;
            }
        } catch (NullPointerException e) {
            oclc = -1;
        }
    }

    public long getOCLC() {
//        System.out.println("In get oclc");
        if (oclc == 0) {
            retrieveOCLC();
        }
        return oclc;
    }

    private void parseFirstDetailSection() {
        String authorSection = doc.getElementsByClass("Ere_sub2_title").first().text();

        translator = FindTranslator(authorSection);
        englishTitle  = FindOriginalTitle(authorSection);
        publisher = FindPublisher(authorSection);
        publishDate = FindPublishDate(authorSection);
        SetAuthors(this, authorSection);

        if(author != "1494")
            retrieveAuthorNumber();
        retrievePublisherNumber();
        if (author2 != null && !author2.equals("")) {
            retrieveAuthor2Number();
        }
    }

    private Book SetAuthors(Book book, String authorSection) {
        Matcher m =  authorsPattern.matcher(authorSection);
        if(m.find()){
            int size = m.groupCount();
            if(size == 2){
                String[] tokens = m.group(1).split(",");
                if(tokens.length > 1){
                    book.author = "1494";
                    book.author2 = "";
                } else {
                    book.author = m.group(2);
                    book.author2 = tokens[0];
                }
            }
            else if(size == 2){
                book.author = m.group(2);
                book.author2 = "";
            }
        }
        return book;

//        "((?:[\\u3131-\\uD79D]+,)*)([\\u3131-\\uD79D ]+) \\(지은이\\)"
    }

    private String FindPublishDate(String authorSection) {
        Matcher m = publishDatePattern.matcher(authorSection);
        if(m.find())
            return m.group(0);
        return "";
//        "\\d{4}-\\d{2}-\\d{2}"
    }

    private String FindPublisher(String authorSection) {
        Matcher m = publisherPattern.matcher(authorSection);
        if(m.find())
            return m.group(1);
        return "";
//        "\\)([\\u3131-\\uD79DA-Za-z ]+)(?:\\d{4}-\\d{2}-\\d{2})"
    }

    //TODO implement these
    private String FindOriginalTitle(String authorSection) {
        Matcher m = originalTitlePattern.matcher(authorSection);
        if(m.find())
            return m.group(1);
        return "";
//        원제 : ([\\p{L} ]+)(?: \\( \\d{4}년\\))?
    }

    private String FindTranslator(String authorSection) {
        Matcher m = translatorPattern.matcher(authorSection);
        if(m.find())
            return m.group(1);
        return "";
        //([\\u3131-\\uD79D]{3}) \\(옮긴이\\)
    }

    public String getAuthor() {
        if (author == null) {
            parseFirstDetailSection();
        }
        return author;
    }

    public String getEnglishTitle() {
        if (englishTitle == null) {
            parseFirstDetailSection();
        }
        return englishTitle;
    }

    public String getPublisher() {
        if (publisher == null) {
            parseFirstDetailSection();
        }
        return publisher;
    }

    public String getTranslator() {
        if (translator == null) {
            parseFirstDetailSection();
        }
        return translator;
    }

    public String getAuthor2() {
        if (author2 == null) {
            parseFirstDetailSection();
        }
        return author2;
    }

    private void parseAuthor(String authorToken) {
        author = authorToken;

        if (author.contains("(")) {
            author = trimParentheses(author);
        } else {
            author = author.trim();
        }
    }

    private void parseEnglishTitle(String englishTitleToken) {
        if (englishTitleToken.contains("(")) {
            englishTitle = trimParentheses(englishTitleToken);
        } else {
            englishTitle = englishTitleToken.trim();
        }
    }

    private void parseAuthor2(String author2Token, int count) {
        if (count == 2) {
            author2 = trimParentheses(author2Token);
        } else {
            author = "1494";
            author2 = "";
        }
    }

    private String trimParentheses(String trim) {
        trim = trim.replace(
                trim.substring(
                        trim.indexOf("("), trim.length()),
                 "")
                .trim();
        return trim;
    }

    private String unicode(String name) {
        String unicodeName;
        try {
            unicodeName = URLEncoder.encode(
                    name, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.toString());
            unicodeName = name;
        }
        return unicodeName;
    }

    //TODO fix if wording
    private void retrieveAuthorNumber() {
//        String url = makeURLAuthor(getAuthor());
//        String url = makeURL(
//                "https://www.bookswindow.com/admin/author/manage/keyword/",
//                getAuthor());
//        String url =
//                "https://www.bookswindow.com/admin/author/manage/keyword/"
//                + unicode(getAuthor());
        Element element = retrieveElementAuthorPublisher(makeURLAuthor(getAuthor()));
        if (element != null) {
            author = element.text();
        }
    }

    private void retrievePublisherNumber() {
        String url = makeURL(
                "https://www.bookswindow.com/admin/mfg/manage/keyword/",
                getPublisher());
//        String url = "https://www.bookswindow.com/admin/mfg/manage/keyword/"
//                + unicode(getPublisher());
        Element element = retrieveElementAuthorPublisher(url);
        if (element != null) {
            publisher = element.text();
        }
    }

    private void retrieveAuthor2Number() {
//        String url = makeURLAuthor(getAuthor2());
//        String url = makeURL(
//                "https://www.bookswindow.com/admin/author/manage/keyword/",
//                getAuthor2());
//        String url =
//                "https://www.bookswindow.com/admin/author/manage/keyword/"
//                + unicode(getAuthor2());
        Element element = retrieveElementAuthorPublisher(makeURLAuthor(getAuthor2()));
        if (element != null) {
            author2 = element.text();
        }
    }

    private Element retrieveElementAuthorPublisher(String url) {
        return retrieveElement(url, "style", "max-width:256px; max-height:256px; overflow:auto; whitespace:nowrap;");
//        return Login.getDocument(url).
//                getElementsByAttributeValueMatching(
//                        "style", "max-width:256px; max-height:256px; overflow:auto; whitespace:nowrap;").first();
    }

    // Search books by keyword
    private Element retrieveElementISBN(String keyword) {
        return retrieveElement(
                makeURLTitle(keyword),
                "style", "margin-top:10px; margin-bottom:10px;");
    }

    private Element retrieveElement(String url, String attr, String value) {
        return Login.getDocument(url).
                getElementsByAttributeValueMatching(attr, value).first();
    }

    private String makeURL(String url, String ending) {
        return url + unicode(ending);
    }

    private String makeURLAuthor(String author) {
        return makeURL("https://www.bookswindow.com/admin/author/manage/keyword/", author);
    }

    private String makeURLTitle(String title) {
        return makeURL("https://www.bookswindow.com/admin/product_core/manage/keyword/", title);
    }

    public boolean titleExists() {
//        System.out.print("checking title");
        return retrieveElementISBN(String.valueOf(getISBN())) != null;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public String getDescription() {
        if (description == null) {
            scrapeLazyDescription(this);
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Book scrapeLazyDescription(Book book) {
        Map<String, String> map = new HashMap<>();
        Document doc;
        map.put("Referer", book.getLocationUrl());
        if (book.getkIsbn() != null) {
            doc = Connect.connectToURLwithHeaders(
                    createLazyDescriptionUrl(String.valueOf(book.getkIsbn())), map);
        } else {
            doc = Connect.connectToURLwithHeaders(
                    createLazyDescriptionUrl(book.getIsbnString()), map);
        }

        book.setDescription(findDescription(doc));
//        Element element = doc.getElementsByAttributeValue("style", "padding: 10px 0 10px 0").first();
//        if(element == null) {
//            element = doc.getElementsByClass("p_textbox").eq(1).first();
//            if(element == null) 
//                book.setDescription("");
//            else book.setDescription(element.wholeText());
//        } else book.setDescription(element.wholeText());
        return book;
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

    private String findCategory(Book book, Document doc) {
        return doc.getElementById("ulCategory").getElementsByTag("li").first().getElementsByTag("a").get(1).text();
//        return doc.getElementsByClass("p_categorize")
//                .first().getElementsByTag("a").get(1).text().trim();
    }

    private Book scrapeLazyAuthor(Book book) {

        if (book.getEnglishTitle() == "") {
            book.setAuthorOriginal("");
            return book;
        }

        Map<String, String> map = new HashMap<>();
        Document doc;

        map.put("Referer", book.getLocationUrl());
        doc = Connect.connectToURLwithHeaders(
                createLazyAuthorUrl(String.valueOf(book.getkIsbn())), map);
        book.setAuthorOriginal(findAuthorOriginal(doc));
        return book;
    }

    private String createLazyAuthorUrl(String details) {
        return "https://www.aladin.co.kr/shop/product/getContents.aspx?ISBN="
                + details + "&name=AuthorInfo&type=0&date=11";
    }

    private String findAuthorOriginal(Document doc) {
        try {
            String result = doc.getElementsByClass("Ere_fs18 Ere_sub_gray3 Ere_str").first().text().trim();
            String regex = "\\(([^()]+)\\)";
            Matcher m = Pattern.compile(regex).matcher(result);
            if (m.find()) {
                return m.group(1);
            } else return "";
        } catch (NullPointerException e) {
            logger.log(Level.INFO, "Couldn't find original Author: "+ doc.location());
            return "";
        }
    }

    /**
     * @return the category
     */
    public String getCategory() {
        if (category == null) {
            this.setCategory(findCategory(this, doc));
        }
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the authorOriginal
     */
    public String getAuthorOriginal() {
        if (authorOriginal == null) {
            scrapeLazyAuthor(this);
        }
        return authorOriginal;
    }

    /**
     * @param authorOriginal the authorOriginal to set
     */
    public void setAuthorOriginal(String authorOriginal) {
        this.authorOriginal = authorOriginal;
    }

    /**
     * @return the locationUrl
     */
    public String getLocationUrl() {
        return locationUrl;
    }

    public String getkIsbn() {
        if(kIsbn == null)
            kIsbn = doc.getElementsByAttributeValueMatching("property", "books:isbn").attr("content");
        return kIsbn;
    }


    /**
     * @param locationUrl the locationUrl to set
     */
    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

}
