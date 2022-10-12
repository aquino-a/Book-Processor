package com.aquino.webParser;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Publisher;
import com.aquino.webParser.utilities.Login;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BookWindowServiceImpl implements BookWindowService {

    private static final String ADD_AUTHOR_URL = "https://www.bookswindow.com/admin/author/ajax/add";
    private static final String ADD_PUBLISHER_URL = "https://www.bookswindow.com/admin/mfg/ajax/add";
    private static final String ADD_LANGUAGE_URL = "https://www.bookswindow.com/admin/shared/ajax/language_obj_add_process";
    private static final String SET_NATIVE_URL = "https://www.bookswindow.com/admin/shared/ajax/language_obj_set_native_process";
    private static final String AUTHOR_ROLE = "100";
    private static final String PUBLISHER_ROLE = "100";
    private static final String ENGLISH_LANG_CODE = "1000";
    private static final String AUTHOR_EDIT_URL_FORMAT = "https://www.bookswindow.com/admin/author/%s/edit/main";
    private static final String PUBLISHER_EDIT_URL_FORMAT = "https://www.bookswindow.com/admin/mfg/%s/edit/main";
    public static final Pattern AUTHOR_NUMBER_REGEX = Pattern.compile("\"redirect_url\":\"\\\\/admin\\\\/author\\\\/(\\d+)\\\\/edit\\\\/main");
    public static final Pattern PUBLISHER_NUMBER_REGEX = Pattern.compile("\"redirect_url\":\"\\\\/admin\\\\/mfg\\\\/(\\d+)\\\\/edit\\\\/main");

    private static final Map<String, String> ADD_AUTHOR_LANGUAGE_PARAMS = Map.of(
        "input_arr[first_name_romanized]", "",
        "input_arr[last_name_romanized]", "",
        "input_arr[description]", "",
        "opt_param[table]", "author", //don't change
        "opt_param[field]", "language_obj", //don't change
        "opt_param[has_native]", "1", //don't change
        "opt_param[info_arr][0][key]", "first_name"
    );

    private static final Map<String, String> SET_NATIVE_PARAMS = Map.of(
        "native_lang", "langCode",
        "opt_param[table]", "author", //don't change
        "opt_param[pk]", "authorId",
        " opt_param[field]", "language_obj",
        "opt_param[has_native]", "1"
    );

    private static final Map<String, String> ADD_PUBLISHER_LANG_PARAMS = Map.of(
        "input_arr[prj_language]", "langCode",
        "opt_param[pk]", "id",
        "input_arr[name]", "pubName",
        "opt_param[table]", "mfg", //don't change
        "opt_param[field]", "language_obj",
        "opt_param[has_native]", "1"
    );

    @Override
    public String getAuthorLink(String id) {
        return String.format(AUTHOR_EDIT_URL_FORMAT, id);
    }

    @Override
    public String getPublisherLink(String id) {
        return String.format(PUBLISHER_EDIT_URL_FORMAT, id);
    }

    @Override
    public Book findIds(Book book) {
        SetAuthor(book);
        SetAuthor2(book);
        SetPublisher(book);
        return book;
    }

    private void SetAuthor(Book book) {
        var parts = findAuthorId(book.getAuthor());
        if (parts.length > 0)
            book.setAuthorId(Integer.parseInt(parts[0]));
        if (parts.length > 2)
            book.setAuthorBooks(String.join(" ", parts[1], parts[2]));
    }

    private void SetAuthor2(Book book) {
        var parts = findAuthorId(book.getAuthor2());
        if (parts.length > 0)
            book.setAuthor2Id(Integer.parseInt(parts[0]));
        if (parts.length > 2)
            book.setAuthor2Books(String.join(" ", parts[1], parts[2]));
    }

    private void SetPublisher(Book book) {
        var parts = findPublisherId(book.getPublisher());
        if (parts.length > 0)
            book.setPublisherId(Integer.parseInt(parts[0]));
        if (parts.length > 1)
            book.setPublisherBooks(parts[1]);
    }

    @Override
    public String[] findPublisherId(String publisher) {
        String url = makeURL("https://www.bookswindow.com/admin/mfg/manage/keyword/", publisher);
        var elements = retrieveElementAuthorPublisher(url);
        if (elements != null) {
            return elements.stream().map((e -> e.text())).toArray(String[]::new);
        } else return new String[]{"-1", "", ""};
    }

    @Override
    public String[] findAuthorId(String author) {
        if (author == null || author.equals("") || author.equals("1494"))
            return new String[]{"-1", "", ""};
        var elements = retrieveElementAuthorPublisher(makeURLAuthor(author));
        if (elements != null) {
            return elements.stream().map((e -> e.text())).toArray(String[]::new);
        } else return new String[]{"-1", "", ""};
    }

    private Elements retrieveElementAuthorPublisher(String url) {
        return retrieveElements(url, "style", "max-width:256px; max-height:256px; overflow:auto; whitespace:nowrap;");
    }

    private Elements retrieveElements(String url, String attr, String value) {
        try {
            return Login.getDocument(url).
                getElementsByAttributeValueMatching(attr, value);
        } catch (NullPointerException e) {
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
        unicodeName = URLEncoder.encode(
            name, StandardCharsets.UTF_8).replace("+", "%20");
        return unicodeName;
    }

    @Override
    public boolean doesBookExist(String isbn) {
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

        var addAuthorDoc = Login.postBody(
            ADD_AUTHOR_URL,
            Map.of("first_name", URLEncoder.encode(author.getEnglishFirstName(), StandardCharsets.UTF_8),
                "middle_name", "",
                "last_name", URLEncoder.encode(author.getEnglishLastName(), StandardCharsets.UTF_8),
                "contributor_role", AUTHOR_ROLE));

        var text = addAuthorDoc.html();
        var matcher = AUTHOR_NUMBER_REGEX.matcher(addAuthorDoc.html());
        int result = -1;
        if (!matcher.find()) {
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
        var data = new HashMap<>(ADD_AUTHOR_LANGUAGE_PARAMS);
        data.put("input_arr[prj_language]", ENGLISH_LANG_CODE);
        data.put("input_arr[first_name]", author.getEnglishFirstName());
        data.put("input_arr[last_name]", author.getEnglishLastName());
        data.put("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(ADD_LANGUAGE_URL, data);
    }

    private void AddNativeLanguage(int id, Author author) {
        var data = new HashMap<>(ADD_AUTHOR_LANGUAGE_PARAMS);
        data.put("input_arr[prj_language]", author.getLanguage().LanguageCode);
        data.put("input_arr[first_name]", author.getNativeFirstName());
        data.put("input_arr[last_name]", author.getNativeLastName());
        data.put("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(ADD_LANGUAGE_URL, data);
    }

    private void SetNativeLanguage(int id, Author author) {
        var data = new HashMap<>(SET_NATIVE_PARAMS);
        data.replace("native_lang", author.getLanguage().LanguageCode);
        data.replace("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(SET_NATIVE_URL, data);
    }

//    await fetch("https://www.bookswindow.com/admin/mfg/ajax/add", {
//        "credentials": "include",
//            "headers": {
//            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
//                "Accept": "application/json, text/javascript, */*; q=0.01",
//                "Accept-Language": "en-US,en;q=0.5",
//                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
//                "X-Requested-With": "XMLHttpRequest"
//        },
//        "referrer": "https://www.bookswindow.com/admin/mfg/add",
//            "body": "title=pub4",
//            "method": "POST",
//            "mode": "cors"
//    });

//    {"status":"success","message":"Successfully Added Mfg","redirect_url":"\/admin\/mfg\/101073\/edit\/main"}
//


//    fetch("https://www.bookswindow.com/admin/shared/ajax/language_obj_add_process", {
//        "headers": {
//            "accept": "application/json, text/javascript, */*; q=0.01",
//                "accept-language": "en-US,en;q=0.7",
//                "cache-control": "no-cache",
//                "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
//                "pragma": "no-cache",
//                "sec-fetch-dest": "empty",
//                "sec-fetch-mode": "cors",
//                "sec-fetch-site": "same-origin",
//                "sec-gpc": "1",
//                "x-requested-with": "XMLHttpRequest",
//                "cookie": "ci_session=a%3A4%3A%7Bs%3A10%3A%22session_id%22%3Bs%3A24%3A%22050d019d3085c7187f21c306%22%3Bs%3A10%3A%22ip_address%22%3Bs%3A10%3A%2210.0.251.1%22%3Bs%3A10%3A%22user_agent%22%3Bs%3A111%3A%22Mozilla%2F5.0+%28Windows+NT+10.0%3B+Win64%3B+x64%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Chrome%2F106.0.0.0+Safari%2F537.36%22%3Bs%3A13%3A%22last_activity%22%3Bi%3A1665456111%3B%7D026e339658d6de18a8df9e52e8fcbeee",
//                "Referer": "https://www.bookswindow.com/admin/mfg/101070/edit/main",
//                "Referrer-Policy": "strict-origin-when-cross-origin"
//        },
//        "body": "input_arr%5Bprj_language%5D=4000&input_arr%5Bname%5D=%ED%8E%8D1&input_arr%5Bname_romanized%5D=Pub1&input_arr%5Bdescription%5D=&opt_param%5Btable%5D=mfg&opt_param%5Bpk%5D=101070&opt_param%5Bfield%5D=language_obj&opt_param%5Bhas_native%5D=1&opt_param%5Binfo_arr%5D%5B0%5D%5Bkey%5D=name&opt_param%5Binfo_arr%5D%5B0%5D%5Blabel%5D=Name&opt_param%5Binfo_arr%5D%5B0%5D%5Brequired%5D=true&opt_param%5Binfo_arr%5D%5B0%5D%5Bclass%5D=input-xlarge&opt_param%5Binfo_arr%5D%5B1%5D%5Bkey%5D=name_romanized&opt_param%5Binfo_arr%5D%5B1%5D%5Blabel%5D=Romanized&opt_param%5Binfo_arr%5D%5B1%5D%5Bclass%5D=input-xlarge&opt_param%5Binfo_arr%5D%5B1%5D%5Binput_type%5D=input_indented&opt_param%5Binfo_arr%5D%5B2%5D%5Bkey%5D=description&opt_param%5Binfo_arr%5D%5B2%5D%5Blabel%5D=Description&opt_param%5Binfo_arr%5D%5B2%5D%5Bclass%5D=input-xxlarge&opt_param%5Binfo_arr%5D%5B2%5D%5Binput_type%5D=textarea&opt_param%5Bsort_arr%5D%5B%5D=name&opt_param%5Bsort_arr%5D%5B%5D=asc",
//            "method": "POST"
//    });
//
//    input_arr[prj_language]: 4000
//    input_arr[name]: 펍1
//    input_arr[name_romanized]: Pub1
//    input_arr[description]:
//    opt_param[table]: mfg
//    opt_param[pk]: 101070
//    opt_param[field]: language_obj
//    opt_param[has_native]: 1
//    opt_param[info_arr][0][key]: name
//    opt_param[info_arr][0][label]: Name
//    opt_param[info_arr][0][required]: true
//    opt_param[info_arr][0][class]: input-xlarge
//    opt_param[info_arr][1][key]: name_romanized
//    opt_param[info_arr][1][label]: Romanized
//    opt_param[info_arr][1][class]: input-xlarge
//    opt_param[info_arr][1][input_type]: input_indented
//    opt_param[info_arr][2][key]: description
//    opt_param[info_arr][2][label]: Description
//    opt_param[info_arr][2][class]: input-xxlarge
//    opt_param[info_arr][2][input_type]: textarea
//    opt_param[sort_arr][]: name
//    opt_param[sort_arr][]: asc

//    fetch("https://www.bookswindow.com/admin/shared/ajax/language_obj_set_native_process", {
//        "headers": {
//            "accept": "application/json, text/javascript, */*; q=0.01",
//                "accept-language": "en-US,en;q=0.7",
//                "cache-control": "no-cache",
//                "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
//                "pragma": "no-cache",
//                "sec-fetch-dest": "empty",
//                "sec-fetch-mode": "cors",
//                "sec-fetch-site": "same-origin",
//                "sec-gpc": "1",
//                "x-requested-with": "XMLHttpRequest"
//        },
//        "referrer": "https://www.bookswindow.com/admin/mfg/101070/edit/main",
//            "referrerPolicy": "strict-origin-when-cross-origin",
//            "body": "native_lang=4000&opt_param%5Btable%5D=mfg&opt_param%5Bpk%5D=101070&opt_param%5Bfield%5D=language_obj&opt_param%5Bhas_native%5D=1&opt_param%5Binfo_arr%5D%5B0%5D%5Bkey%5D=name&opt_param%5Binfo_arr%5D%5B0%5D%5Blabel%5D=Name&opt_param%5Binfo_arr%5D%5B0%5D%5Brequired%5D=true&opt_param%5Binfo_arr%5D%5B0%5D%5Bclass%5D=input-xlarge&opt_param%5Binfo_arr%5D%5B1%5D%5Bkey%5D=name_romanized&opt_param%5Binfo_arr%5D%5B1%5D%5Blabel%5D=Romanized&opt_param%5Binfo_arr%5D%5B1%5D%5Bclass%5D=input-xlarge&opt_param%5Binfo_arr%5D%5B1%5D%5Binput_type%5D=input_indented&opt_param%5Binfo_arr%5D%5B2%5D%5Bkey%5D=description&opt_param%5Binfo_arr%5D%5B2%5D%5Blabel%5D=Description&opt_param%5Binfo_arr%5D%5B2%5D%5Bclass%5D=input-xxlarge&opt_param%5Binfo_arr%5D%5B2%5D%5Binput_type%5D=textarea&opt_param%5Bsort_arr%5D%5B%5D=name&opt_param%5Bsort_arr%5D%5B%5D=asc&native_lang_status=1",
//            "method": "POST",
//            "mode": "cors",
//            "credentials": "include"
//    });
//    native_lang: 4000
//    opt_param[table]: mfg
//    opt_param[pk]: 101070
//    opt_param[field]: language_obj
//    opt_param[has_native]: 1
//    opt_param[info_arr][0][key]: name
//    opt_param[info_arr][0][label]: Name
//    opt_param[info_arr][0][required]: true
//    opt_param[info_arr][0][class]: input-xlarge
//    opt_param[info_arr][1][key]: name_romanized
//    opt_param[info_arr][1][label]: Romanized
//    opt_param[info_arr][1][class]: input-xlarge
//    opt_param[info_arr][1][input_type]: input_indented
//    opt_param[info_arr][2][key]: description
//    opt_param[info_arr][2][label]: Description
//    opt_param[info_arr][2][class]: input-xxlarge
//    opt_param[info_arr][2][input_type]: textarea
//    opt_param[sort_arr][]: name
//    opt_param[sort_arr][]: asc
//    native_lang_status: 1

//fetch("https://www.bookswindow.com/admin/shared/ajax/language_obj_add_process", {
//  "headers": {
//    "accept": "application/json, text/javascript, */*; q=0.01",
//    "accept-language": "en-US,en;q=0.7",
//    "cache-control": "no-cache",
//    "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
//    "pragma": "no-cache",
//    "sec-fetch-dest": "empty",
//    "sec-fetch-mode": "cors",
//    "sec-fetch-site": "same-origin",
//    "sec-gpc": "1",
//    "x-requested-with": "XMLHttpRequest"
//  },
//  "referrer": "https://www.bookswindow.com/admin/mfg/101070/edit/main",
//  "referrerPolicy": "strict-origin-when-cross-origin",
//  "body": "input_arr%5Bprj_language%5D=1000&input_arr%5Bname%5D=pub1&input_arr%5Bname_romanized%5D=&input_arr%5Bdescription%5D=&opt_param%5Btable%5D=mfg&opt_param%5Bpk%5D=101070&opt_param%5Bfield%5D=language_obj&opt_param%5Bhas_native%5D=1&opt_param%5Binfo_arr%5D%5B0%5D%5Bkey%5D=name&opt_param%5Binfo_arr%5D%5B0%5D%5Blabel%5D=Name&opt_param%5Binfo_arr%5D%5B0%5D%5Brequired%5D=true&opt_param%5Binfo_arr%5D%5B0%5D%5Bclass%5D=input-xlarge&opt_param%5Binfo_arr%5D%5B1%5D%5Bkey%5D=name_romanized&opt_param%5Binfo_arr%5D%5B1%5D%5Blabel%5D=Romanized&opt_param%5Binfo_arr%5D%5B1%5D%5Bclass%5D=input-xlarge&opt_param%5Binfo_arr%5D%5B1%5D%5Binput_type%5D=input_indented&opt_param%5Binfo_arr%5D%5B2%5D%5Bkey%5D=description&opt_param%5Binfo_arr%5D%5B2%5D%5Blabel%5D=Description&opt_param%5Binfo_arr%5D%5B2%5D%5Bclass%5D=input-xxlarge&opt_param%5Binfo_arr%5D%5B2%5D%5Binput_type%5D=textarea&opt_param%5Bsort_arr%5D%5B%5D=name&opt_param%5Bsort_arr%5D%5B%5D=asc",
//  "method": "POST",
//  "mode": "cors",
//  "credentials": "include"
//});

    //    input_arr[prj_language]: 1000
//    input_arr[name]: pub1
//    input_arr[name_romanized]:
//    input_arr[description]:
//    opt_param[table]: mfg
//    opt_param[pk]: 101070
//    opt_param[field]: language_obj
//    opt_param[has_native]: 1
//    opt_param[info_arr][0][key]: name
//    opt_param[info_arr][0][label]: Name
//    opt_param[info_arr][0][required]: true
//    opt_param[info_arr][0][class]: input-xlarge
//    opt_param[info_arr][1][key]: name_romanized
//    opt_param[info_arr][1][label]: Romanized
//    opt_param[info_arr][1][class]: input-xlarge
//    opt_param[info_arr][1][input_type]: input_indented
//    opt_param[info_arr][2][key]: description
//    opt_param[info_arr][2][label]: Description
//    opt_param[info_arr][2][class]: input-xxlarge
//    opt_param[info_arr][2][input_type]: textarea
//    opt_param[sort_arr][]: name
//    opt_param[sort_arr][]: asc
    @Override
    public int addPublisher(Publisher publisher) {
        var doc = Login.postBody(
            ADD_PUBLISHER_URL,
            Map.of("title", URLEncoder.encode(publisher.getEnglishName(), StandardCharsets.UTF_8)));

        var matcher = PUBLISHER_NUMBER_REGEX.matcher(doc.html());
        if (!matcher.find()) {
            return -1;
        }

        var result = Integer.parseInt(matcher.group(1).trim());
        AddEnglishLanguage(result, publisher);
        AddNativeLanguage(result, publisher);
        SetNativeLanguage(result, publisher);
        publisher.setId(result);

        return result;
    }

    private void AddEnglishLanguage(int id, Publisher publisher) {
        var data = new HashMap<>(ADD_PUBLISHER_LANG_PARAMS);
        data.put("input_arr[prj_language]", ENGLISH_LANG_CODE);
        data.put("input_arr[name]:", publisher.getEnglishName());
        data.put("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(ADD_LANGUAGE_URL, data);
    }

    private void AddNativeLanguage(int id, Publisher publisher) {
        var data = new HashMap<>(ADD_PUBLISHER_LANG_PARAMS);
        data.put("input_arr[prj_language]", publisher.getLanguage().LanguageCode);
        data.put("input_arr[name]:", publisher.getNativeName());
        data.put("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(ADD_LANGUAGE_URL, data);
    }

    private void SetNativeLanguage(int id, Publisher publisher) {
        var data = new HashMap<>(SET_NATIVE_PARAMS);
        data.replace("native_lang", publisher.getLanguage().LanguageCode);
        data.replace("opt_param[table]", "mfg");
        data.replace("opt_param[pk]", String.valueOf(id));

        var doc = Login.postDocument(SET_NATIVE_URL, data);
    }
}
