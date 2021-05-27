package com.aquino.webParser;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Publisher;
import com.aquino.webParser.utilities.Login;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BookWindowServiceImpl implements BookWindowService {

    private static final String ADD_URL = "https://www.bookswindow.com/admin/author/ajax/add";
    private static final String ADD_LANGUAGE_URL = "https://www.bookswindow.com/admin/shared/ajax/language_obj_add_process";
    private static final String SET_NATIVE_URL = "https://www.bookswindow.com/admin/shared/ajax/language_obj_set_native_process";
    private static final String AUTHOR_ROLE = "100";
    private static final String PUBLISHER_ROLE = "100";
    private static final String ENGLISH_LANG_CODE = "1000";
    private static final String AUTHOR_EDIT_URL_FORMAT = "https://www.bookswindow.com/admin/author/%s/edit/main";
    public static final Pattern AUTHOR_NUMBER_REGEX = Pattern.compile("\"redirect_url\":\"\\\\/admin\\\\/author\\\\/(\\d+)\\\\/edit\\\\/main");
    //add author return url
    //https://www.bookswindow.com/admin/author/(33521)/edit/main

//Ignore all empty strings
//    private static final Map<String, String> ADD_LANGUAGE_PARAMS = Map.of(
//            "input_arr[prj_language]", "langCode",
//            "input_arr[first_name]", "firstName",
//            "input_arr[first_name_romanized]", "",
//            "input_arr[last_name]", "lastName",
//            "input_arr[last_name_romanized]", "",
//            "input_arr[description]", "",
//            "opt_param[table]", "author", //don't change
//            "opt_param[pk]", "authorId",
//            "opt_param[field]", "language_obj", //don't change
//            "opt_param[has_native]", "1", //don't change
//            "opt_param[info_arr][0][key]", "first_name",
//            "opt_param[info_arr][0][label]", "First Name",
//            "opt_param[info_arr][0][required]", "true",
//            "opt_param[info_arr][0][class]", "input-xlarge",
//            "opt_param[info_arr][1][key]", "first_name_romanized",
//            "opt_param[info_arr][1][label]", "Romanized",
//            "opt_param[info_arr][1][class]", "input-xlarge",
//            "opt_param[info_arr][1][input_type]", "input_indented",
//            "opt_param[info_arr][2][key]", "last_name",
//            "opt_param[info_arr][2][label]", "Last Name",
//            "opt_param[info_arr][2][class]", "input-xlarge",
//            "opt_param[info_arr][3][key]", "last_name_romanized",
//            "opt_param[info_arr][3][label]", "Romanized",
//            "opt_param[info_arr][3][class]", "input-xlarge",
//            "opt_param[info_arr][3][input_type]", "input_indented",
//            "opt_param[info_arr][4][key]", "description",
//            "opt_param[info_arr][4][label]", "Intro / Description",
//            "opt_param[info_arr][4][class]", "input-xxlarge",
//            "opt_param[info_arr][4][input_type]", "textarea",
//            "opt_param[sort_arr][]", "first_name",
//            "opt_param[sort_arr][]", "asc"
//            );

    private static final Map<String, String> ADD_LANGUAGE_PARAMS1 = Map.of(
            "input_arr[first_name_romanized]", "",
            "input_arr[last_name_romanized]", "",
            "input_arr[description]", "",
            "opt_param[table]", "author", //don't change
            "opt_param[field]", "language_obj", //don't change
            "opt_param[has_native]", "1", //don't change
            "opt_param[info_arr][0][key]", "first_name"
    );


    private static final Map<String, String> ADD_LANGUAGE_PARAMS2 = Map.of(
            "opt_param[info_arr][0][label]", "First Name",
            "opt_param[info_arr][0][required]", "true",
            "opt_param[info_arr][0][class]", "input-xlarge",
            "opt_param[info_arr][1][key]", "first_name_romanized",
            "opt_param[info_arr][1][label]", "Romanized",
            "opt_param[info_arr][1][class]", "input-xlarge",
            "opt_param[info_arr][1][input_type]", "input_indented",
            "opt_param[info_arr][2][key]", "last_name",
            "opt_param[info_arr][2][label]", "Last Name",
            "opt_param[info_arr][2][class]", "input-xlarge"
    );


    private static final Map<String, String> ADD_LANGUAGE_PARAMS3 = Map.of(
            "opt_param[info_arr][3][key]", "last_name_romanized",
            "opt_param[info_arr][3][label]", "Romanized",
            "opt_param[info_arr][3][class]", "input-xlarge",
            "opt_param[info_arr][3][input_type]", "input_indented",
            "opt_param[info_arr][4][key]", "description",
            "opt_param[info_arr][4][label]", "Intro / Description",
            "opt_param[info_arr][4][class]", "input-xxlarge",
            "opt_param[info_arr][4][input_type]", "textarea",
            "opt_param[sort_arr][]", "first_name"
    );

    private static Map<String, String> GetAddLanguageParams(){
        var m = new HashMap<String, String>(ADD_LANGUAGE_PARAMS1);
        m.putAll(ADD_LANGUAGE_PARAMS2);
        m.putAll(ADD_LANGUAGE_PARAMS3);
        return m;
    }

    @Override
    public String getAuthorLink(String id) {
        return String.format(AUTHOR_EDIT_URL_FORMAT, id);
    }

    private static final Map<String, String> SET_NATIVE_PARAMS = Map.of(
            "native_lang", "langCode",
            "opt_param[table]", "author", //don't change
            "opt_param[pk]", "authorId",
            " opt_param[field]", "language_obj",
            "opt_param[has_native]", "1"
    );


    //add author // publisher
//    first_name: testttt1
//    middle_name:
//    last_name: testttt1
//    contributor_role: 100

    //language - engish
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

    //add language japanese
//    input_arr[prj_language]: 3000
//    input_arr[first_name]: testttt1
//    input_arr[first_name_romanized]:
//    input_arr[last_name]: testttt1
//    input_arr[last_name_romanized]:
//    input_arr[description]:
//    opt_param[table]: author
//    opt_param[pk]: 33535
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

    //native - english
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

//    native_lang: 3000
//    opt_param[table]: author
//    opt_param[pk]: 33535
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
        SetAuthor(book);
        SetAuthor2(book);
        SetPublisher(book);
        return book;
    }

    private void SetAuthor(Book book) {
        var parts = findAuthorId(book.getAuthor());
        if(parts.length > 0)
            book.setAuthorId(Integer.parseInt(parts[0]));
        if(parts.length > 2)
        book.setAuthorBooks(String.join(" ", parts[1], parts[2]));
    }

    private void SetAuthor2(Book book) {
        var parts = findAuthorId(book.getAuthor2());
        if(parts.length > 0)
            book.setAuthor2Id(Integer.parseInt(parts[0]));
        if(parts.length > 2)
            book.setAuthor2Books(String.join(" ", parts[1], parts[2]));
    }

    private void SetPublisher(Book book) {
        var parts = findPublisherId(book.getPublisher());
        if(parts.length > 0)
            book.setPublisherId(Integer.parseInt(parts[0]));
        if(parts.length > 1)
            book.setPublisherBooks(parts[1]);
    }

    @Override
    public String[] findPublisherId(String publisher){
        String url = makeURL("https://www.bookswindow.com/admin/mfg/manage/keyword/",publisher);
        var elements = retrieveElementAuthorPublisher(url);
        if (elements != null) {
            return elements.stream().map((e -> e.text())).toArray(String[]::new);
        } else return new String[]{ "-1", "", ""};
    }

    @Override
    public String[] findAuthorId(String author){
        if(author == null || author.equals("") || author.equals("1494"))
            return new String[]{ "-1", "", ""};
        var elements = retrieveElementAuthorPublisher(makeURLAuthor(author));
        if (elements != null) {
            return elements.stream().map((e -> e.text())).toArray(String[]::new);
        } else return new String[]{ "-1", "", ""};
    }

    private Elements retrieveElementAuthorPublisher(String url) {
        return retrieveElements(url, "style", "max-width:256px; max-height:256px; overflow:auto; whitespace:nowrap;");
    }

    private Elements retrieveElements(String url, String attr, String value) {
        try{
            return Login.getDocument(url).
                    getElementsByAttributeValueMatching(attr, value);
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

    @Override
    public boolean doesBookExist(String isbn){
        return retrieveElementISBN(isbn) != null;
    }

    private Element retrieveElementISBN(String keyword) {
        return retrieveElements(
                makeURLTitle(keyword),
                "style", "margin-top:10px; margin-bottom:10px;").first();
    }

    private String makeURLTitle(String title) {
        return makeURL("https://www.bookswindow.com/admin/product_core/manage/keyword/", title);
    }

    @Override
    public int addAuthor(Author author) {

        var addAuthorDoc = Login.postDocument(ADD_URL,
                Map.of("first_name",author.getEnglishFirstName(),"middle_name", "","last_name", author.getEnglishLastName(), "contributor_role", AUTHOR_ROLE));
        var text = addAuthorDoc.html();
        var matcher = AUTHOR_NUMBER_REGEX.matcher(addAuthorDoc.html());
        int result = -1;
        if(!matcher.find()) {
            return result;
        }
        result = Integer.parseInt(matcher.group(1).trim());
        AddEnglishLanguage(result, author);
        AddNativeLanguage(result, author);
        SetNativeLanguage(result, author);
        author.setId(result);
        return result;
    }

    private void AddEnglishLanguage(int id, Author author) {
        var data = GetAddLanguageParams();
        data.put("input_arr[prj_language]", ENGLISH_LANG_CODE);
        data.put("input_arr[first_name]", author.getEnglishFirstName());
        data.put("input_arr[last_name]", author.getEnglishLastName());
        data.put("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(ADD_LANGUAGE_URL, data);
    }

    private void AddNativeLanguage(int id, Author author) {
        var data = GetAddLanguageParams();
        data.put("input_arr[prj_language]", author.getLanguage().LanguageCode);
        data.put("input_arr[first_name]", author.getNativeFirstName());
        data.put("input_arr[last_name]", author.getNativeLastName());
        data.put("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(ADD_LANGUAGE_URL, data);
    }

    private void SetNativeLanguage(int id, Author author) {
        var data = new HashMap<String, String>(SET_NATIVE_PARAMS);
        data.replace("native_lang", author.getLanguage().LanguageCode);
        data.replace("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(SET_NATIVE_URL, data);
    }

    @Override
    public int addPublisher(Publisher publisher) {
        throw new NotImplementedException("");
    }

}
