package com.aquino.webParser;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Publisher;
import com.aquino.webParser.utilities.Login;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

//TODO implement
public class BookWindowServiceImpl implements BookWindowService {

    private static final String ADD_URL = "https://www.bookswindow.com/admin/author/ajax/add";
    private static final String ADD_LANGUAGE_URL = "https://www.bookswindow.com/admin/shared/ajax/language_obj_add_process";
    private static final String SET_NATIVE_URL = "https://www.bookswindow.com/admin/shared/ajax/language_obj_set_native_process";


    //add author // publisher
//    first_name: testttt1
//    middle_name:
//    last_name: testttt1
//    contributor_role: 100

    //language
//    input_arr[prj_language]: 1000
//    input_arr[first_name]: testttt1
//    input_arr[first_name_romanized]:
//    input_arr[last_name]: testttt1
//    input_arr[last_name_romanized]:
//    input_arr[description]:
//    opt_param[table]: author
//    opt_param[pk]: 33468
//    opt_param[field]: language_obj
//    opt_param[has_native]: 1
//    opt_param[info_arr][0][key]: first_name
//    opt_param[info_arr][0][label]: First Name
//    opt_param[info_arr][0][required]: true
//    opt_param[info_arr][0][class]: input-xlarge
//    opt_param[info_arr][1][key]: first_name_romanized
//    opt_param[info_arr][1][label]: Romanized
//    opt_param[info_arr][1][class]: input-xlarge
//    opt_param[info_arr][1][input_type]: input_indented
//    opt_param[info_arr][2][key]: last_name
//    opt_param[info_arr][2][label]: Last Name
//    opt_param[info_arr][2][class]: input-xlarge
//    opt_param[info_arr][3][key]: last_name_romanized
//    opt_param[info_arr][3][label]: Romanized
//    opt_param[info_arr][3][class]: input-xlarge
//    opt_param[info_arr][3][input_type]: input_indented
//    opt_param[info_arr][4][key]: description
//    opt_param[info_arr][4][label]: Intro / Description
//    opt_param[info_arr][4][class]: input-xxlarge
//    opt_param[info_arr][4][input_type]: textarea
//    opt_param[sort_arr][]: first_name
//    opt_param[sort_arr][]: asc

    //native
//    native_lang: 1000
//    opt_param[table]: author
//    opt_param[pk]: 33468
//    opt_param[field]: language_obj
//    opt_param[has_native]: 1
//    opt_param[info_arr][0][key]: first_name
//    opt_param[info_arr][0][label]: First Name
//    opt_param[info_arr][0][required]: true
//    opt_param[info_arr][0][class]: input-xlarge
//    opt_param[info_arr][1][key]: first_name_romanized
//    opt_param[info_arr][1][label]: Romanized
//    opt_param[info_arr][1][class]: input-xlarge
//    opt_param[info_arr][1][input_type]: input_indented
//    opt_param[info_arr][2][key]: last_name
//    opt_param[info_arr][2][label]: Last Name
//    opt_param[info_arr][2][class]: input-xlarge
//    opt_param[info_arr][3][key]: last_name_romanized
//    opt_param[info_arr][3][label]: Romanized
//    opt_param[info_arr][3][class]: input-xlarge
//    opt_param[info_arr][3][input_type]: input_indented
//    opt_param[info_arr][4][key]: description
//    opt_param[info_arr][4][label]: Intro / Description
//    opt_param[info_arr][4][class]: input-xxlarge
//    opt_param[info_arr][4][input_type]: textarea
//    opt_param[sort_arr][]: first_name
//    opt_param[sort_arr][]: asc
//    native_lang_status: 1

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
        if(author == null || author.equals("") || author.equals("1494"))
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
        try{
            return Login.getDocument(url).
                    getElementsByAttributeValueMatching(attr, value).first();
        } catch (NullPointerException e){
            return null;
        }
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

    @Override
    public int addAuthor(Author author) {

        throw new NotImplementedException("");
    }

    @Override
    public int addPublisher(Publisher publisher) {
        throw new NotImplementedException("");
    }

}
