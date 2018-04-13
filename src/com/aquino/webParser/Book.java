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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author alex
 */
public class Book {
    private Document doc;
    private String title,publishDate,originalPrice,bookSize,
            cover,publishDateFormatted,bookSizeFormatted,
            imageURL, author, englishTitle, translator,
            publisher, author2, isbnString, description;
    private long isbn,oclc;
    
    private int pages, weight;
    private double originalPriceFormatted;
    
    public Book(String url) {
//            System.out.println("in book constructor");
            doc = Connect.connectToURL(url);
    }

    
    public Document getDoc() {
        return doc;
    }
    private void retrieveTitle() throws IllegalStateException{
        if(doc == null) throw new IllegalStateException("There is no book./n" +
                "Retry with proper url");
        title = doc.getElementsByAttributeValueMatching("name", "twitter:title").attr("content");
    }
    public String getTitle() {
        if(title == null) retrieveTitle();
        return title;
    } 
    
    private void retrieveISBN() {
        try {
            isbnString = doc.getElementsByAttributeValueMatching("property", "og:barcode").attr("content");
            isbn = Long.parseLong(isbnString);
        } catch (NumberFormatException e) {
            isbn = -1;
        }
    }
    public Long getISBN() {
        if(isbn == 0) retrieveISBN();
        return isbn;
    }
    
    public String getIsbnString() {
        return isbnString;
    }
    
    private void retrieveOriginalPrice() {
        originalPrice = doc.getElementsByClass("p_goodstd02").first().text();
        StringBuilder sb = new StringBuilder(originalPrice);
        sb.deleteCharAt(sb.length()-1);
        originalPrice = sb.toString();
    }
    public String getOriginalPrice() {
        if(originalPrice == null) retrieveOriginalPrice();
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
        if(m.find()) publishDate = m.group();
        else System.out.println("No date found");
    }
    public String getPublishDate() {
        if (publishDate == null) retrieveDate();
        return publishDate;
    }
    
    private void retrieveVarious() {
        StringTokenizer st = new StringTokenizer(doc.getElementsByClass("p_goodstd03").text(), " |");
        Pattern number = Pattern.compile("(\\d+)");
        if(st.hasMoreTokens()){
            final String first = st.nextToken();
            if (Character.isDigit(first.charAt(0))){
                cover = "Paperback";
                Matcher m = number.matcher(first);
                if(m.find()) {
                    pages = Integer.parseInt(m.group());
                }
                bookSize = st.nextToken();
            } else {
                if(Pattern.compile("반").matcher(first).find()) cover = "Paperback";
                else cover = "Hardcover";
                Matcher m = number.matcher(st.nextToken());
                if(m.find()) {
                    pages = Integer.parseInt(m.group());
                }
                bookSize = st.nextToken();
            }
            
        }
    }
    public String getType() {
        if(cover == null) retrieveVarious();
        return cover;
    }
    public int getPages() {
        if(pages == 0) retrieveVarious();
        return pages;
    }
    public String getBookSize() {
        if(bookSize == null)retrieveVarious();
        return bookSize;
    }
    private void formatSize() {
        if(bookSize == null)retrieveVarious();
        StringTokenizer st = new StringTokenizer(bookSize,"*m");
        try {
            double first = Double.parseDouble(st.nextToken())/10;
            double second = Double.parseDouble(st.nextToken())/10;
            if(first % 1 != 0 && second % 1 != 0)
                bookSizeFormatted = first +" x " + second;
            else if(first % 1 == 0 && second % 1 == 0)
                bookSizeFormatted = (int)first + " x " + (int)second;
            else if(first % 1 == 0)
                bookSizeFormatted = (int) first + " x " + second;
            else if(second % 1 == 0)
                bookSizeFormatted = first + " x " + (int)second;
        } catch (NumberFormatException e) {
            bookSizeFormatted = "problem";
        }
    }
    
    
    
    public String getBookSizeFormatted() {
        if(bookSizeFormatted == null) formatSize();
        if(bookSizeFormatted.equals("problem")) return bookSize;
        return bookSizeFormatted;
    }
    private void formatDate() {
        if(publishDate == null)retrieveDate();
        StringTokenizer st = new StringTokenizer(publishDate, "-");
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append(st.nextToken());
        sb.insert(0, st.nextToken());
        sb.insert(2,st.nextToken());
        sb.insert(2, '/');
        publishDateFormatted = sb.toString();
    }
    public String getPublishDateFortmatted() {
        if(publishDateFormatted == null) formatDate();
        return publishDateFormatted;
    }
    private String imageUrlCreate(String startUrl) {
        StringBuilder sb = new StringBuilder(startUrl);
        sb.replace(sb.indexOf("cover"), sb.indexOf("cover")+5, "letslook");
        sb.setCharAt(sb.lastIndexOf("_")+1, 'f');
        
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
        if(imageURL == null) retrieveImageUrl();
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
                    getNumberInstance(Locale.KOREA).parse(getOriginalPrice()).doubleValue()/1000;
        }catch (ParseException e) {
        }
    }
    public double getOriginalPriceFormatted() {
        if(originalPriceFormatted == 0) formatOriginalPrice();
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
        if(pages == 0) retrieveVarious();
        if(pages <= 300) {weight = 1; return;}
        if(pages <= 600) {weight = 2; return;}
        if(pages <= 900) {weight = 3;}
    }
    public int getWeight() {
        if(weight == 0) retrieveWeight();
        return weight;
    }
    
    private void retrieveOCLC() {
        Document oclcDoc;
        try {
            if(getISBN() != -1) {
                oclcDoc = Connect.connectToURL("http://www.worldcat.org/search?q="+getISBN());
                oclc = Long.parseLong(oclcDoc.getElementsByClass("oclc_number").first().text());
            } else oclc = -1;
        } catch (NullPointerException e) {
            oclc = -1;
        }
    }
    public long getOCLC() {
//        System.out.println("In get oclc");
        if(oclc == 0) retrieveOCLC();
        return oclc;
    }
    private void retrieveAuthorInfo() {
        String previous = null;
        int count = 1;
        StringTokenizer st = new StringTokenizer(
                doc.getElementsByAttributeValueMatching(
                        "style", "height:25px; padding-right:10px;").text()
                , "|");
        while(st.hasMoreTokens()) {
            if(author == null) {
                parseAuthor(st.nextToken());
                count++;
                //continue;
            }
            String next = st.nextToken();
            if(next.contains("(지은이)")) parseAuthor2(next, count);
                
            if(next.contains("(역자)")) {
                this.translator = next.replace("(역자)", "").trim();
                //continue;
            }
            if(next.contains("원제"))
                parseEnglishTitle(next.replace("원제", ""));
            
            if(next.contains("-") && previous != null && publisher == null) {
                if(previous.indexOf("(") > 1)
                    publisher = trimParentheses(previous);
                else publisher = previous.trim();
            }
            previous = next;
            count++;
        }
        retrieveAuthorNumber();
        retrievePublisherNumber();
        if(author2 != null  && author2 != "") retrieveAuthor2Number();
        if(translator == null) translator = "";
        if(englishTitle == null) englishTitle = "";
        if(author2 == null) author2 = "";
    }
    public String getAuthor() {
        if(author == null) retrieveAuthorInfo();
        return author;
    }
    public String getEnglishTitle() {
        if(englishTitle == null) retrieveAuthorInfo();
        return englishTitle;
    } 
    public String getPublisher() {
        if(publisher == null) retrieveAuthorInfo();
        return publisher;
    }
    public String getTranslator() {
        if(translator == null) retrieveAuthorInfo();
        return translator;
    }
    public String getAuthor2() {
        if (author2 == null) retrieveAuthorInfo();
        return author2;
    }
    private void parseAuthor(String authorToken) {
        author = authorToken;
        
        if (author.contains("(")) 
            author = trimParentheses(author);
        else author = author.trim();
    }
    private void parseEnglishTitle(String englishTitleToken) {
        if(englishTitleToken.contains("("))
            englishTitle = trimParentheses(englishTitleToken);
         else englishTitle = englishTitleToken.trim();
    }
    private void parseAuthor2(String author2Token,int count) {
        if(count == 2) author2 = trimParentheses(author2Token);
        else {
            author = "1494";
            author2 = "";
        }
    }
    private String trimParentheses(String trim) {
        trim = trim.replace(
                            trim.substring(
                                    trim.indexOf("("), trim.length())
                            , "")
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
        if (element != null) author = element.text();
    }
    
    private void retrievePublisherNumber() {
        String url = makeURL(
                "https://www.bookswindow.com/admin/mfg/manage/keyword/",
                getPublisher());
//        String url = "https://www.bookswindow.com/admin/mfg/manage/keyword/"
//                + unicode(getPublisher());
        Element element = retrieveElementAuthorPublisher(url);
        if (element != null) publisher = element.text();
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
        if (element != null) author2 = element.text();
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
    private Element retrieveElement(String url,String attr ,String value) {
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
        if(description == null) scrapeLazyContent(this, doc);
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Book scrapeLazyContent(Book book, Document doc) {
        Map<String, String> map = new HashMap<>();
        map.put("Referer", doc.location());
        doc = Connect.connectToURLwithHeaders(
                createLazyContentUrl(book.getISBN()), map);
        Element element = doc.getElementsByAttributeValue("style", "padding: 10px 0 10px 0").first();
        if(element == null) {
            book.setDescription(doc.getElementsByClass("p_textbox").eq(1).first().wholeText());
        } else book.setDescription(element.wholeText());
        return book;
    }
    
    private String createLazyContentUrl(long details) {
        return "https://www.aladin.co.kr/shop/product/getContents.aspx?ISBN="
                + details + "&name=Introduce&type=0&date=11";
    }
    
}
