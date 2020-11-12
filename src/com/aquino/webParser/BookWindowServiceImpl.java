package com.aquino.webParser;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Publisher;
import com.aquino.webParser.utilities.Login;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

//TODO implement
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

    public static String GetEditAuthorUrl(int id){
        return String.format(AUTHOR_EDIT_URL_FORMAT, String.valueOf(id));
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
