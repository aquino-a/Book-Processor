package com.aquino.webParser;

import com.aquino.webParser.utilities.Login;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

//TODO implement
public class BookWindowServiceImpl implements BookWindowService {

    @Override
    public Book findIds(Book book){
        book.setAuthor(findAuthorId(book.getAuthor()));
        book.setAuthor2(findAuthorId(book.getAuthor2()));
        book.setPublisher(findPublisherId(book.getPublisher()));
        return book;
    }

    @Override
    public String findPublisherId(String publisher){
        String url = makeURL("https://www.bookswindow.com/admin/mfg/manage/keyword/",publisher);
        Element element = retrieveElementAuthorPublisher(url);
        if (element != null) {
            return element.text() + ' ' + publisher;
        } else return publisher;
    }

    @Override
    public String findAuthorId(String author){
        if(author == null || author == "")
            return author;
        Element element = retrieveElementAuthorPublisher(makeURLAuthor(author));
        if (element != null) {
            return element.text() + ' ' + author;
        } else return author;
    }

    private Element retrieveElementAuthorPublisher(String url) {
        return retrieveElement(url, "style", "max-width:256px; max-height:256px; overflow:auto; whitespace:nowrap;");
    }

    private Element retrieveElement(String url, String attr, String value) {
        return Login.getDocument(url).
                getElementsByAttributeValueMatching(attr, value).first();
    }

    private String makeURLAuthor(String author) {
        return makeURL("https://www.bookswindow.com/admin/author/manage/keyword/", author);
    }

    private String makeURL(String url, String ending) {
        return url + unicode(ending);
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

    //TODO
    @Override
    public boolean doesBookExist(String isbn){
        throw new NotImplementedException("TODO");
    }

}
