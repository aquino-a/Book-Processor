package com.aquino.webParser.oclc;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class OclcServiceImpl implements OclcService, BookCreator {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Set<String> SUCCESS_CODES = Set.of("0", "2", "4");
    private static final String ISBN_REQUEST = "http://classify.oclc.org/classify2/Classify?isbn=%s&summary=true";
    private static final String OWI_REQUEST = "http://classify.oclc.org/classify2/Classify?owi=%s";
    private static final String WORLDCAT_REQUEST = "http://www.worldcat.org/api/search?q=%s&audience=&author=" +
        "&content=&datePublished=&inLanguage=&itemSubType=&itemType=&limit=10&offset=1&openAccess=&orderBy=library" +
        "&peerReviewed=&topic=&heldByInstitutionID=&preferredLanguage=eng&relevanceByGeoCoordinates=true" +
        "&lat=35.5625&lon=129.1235";

    private static final String WC_LOCATION_REQUEST = "https://www.worldcat.org/api/iplocation?language=en";
    private static final String WC_TOKEN_REQUEST = "http://www.worldcat.org/_next/data/%s/en/search.json?q=%s";
    private static final Pattern WC_BUILD_PATTERN = Pattern.compile("/_next/static/([a-z0-9]+)/_buildManifest\\.js");
    private static final Pattern WC_TOKEN_PATTERN = Pattern.compile("\"secureToken\":\"([-a-zA-Z0-9._~+/]+=*)\",");

    private boolean firstTime = true;
    private CookieManager cookieManager;
    private String wcBuild;
    private final Map<String, String> classifyCookies = new HashMap<>();

    @Override
    public long findOclc(String isbn) {
        try {
            var owi = GetOwi(isbn);

            var oclc = owi == null
                ? GetWorldCatOclc(isbn)
                : GetOclc(owi);

            return Long.parseLong(oclc);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.info(String.format("Couldn't get OCLC for %s", isbn));
            LOGGER.info(e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Gets the author, author2, and publisher from world cat using the oclc.
     * Does not support searching by isbn.
     *
     * @param oclc the oclc of the book.
     * @return a {@code Book} with author and publisher.
     */
    @Override
    public Book createBookFromIsbn(String oclc) throws IOException {
        try {
            if (firstTime) {
                initializeCookies();
            }

            var json = queryWorldCat("no:" + oclc);
            var root = OBJECT_MAPPER.readTree(json);

            return createBook(root);
        } catch (InterruptedException | URISyntaxException e) {
            LOGGER.info(String.format("Couldn't get book for %s", oclc));
            LOGGER.info(e.getMessage(), e);
            return null;
        }
    }


    /**
     * Tries to get the oclc from worldcat.org.
     *
     * @param isbn the book's isbn.
     * @return the oclc if found
     */
    private String GetWorldCatOclc(String isbn) throws IOException, InterruptedException, URISyntaxException {
        if (firstTime) {
            initializeCookies();
        }

        var json = queryWorldCat("bn:" + isbn);
        var root = OBJECT_MAPPER.readTree(json);

        return findOclc(root);
    }

    /**
     * Queries worldcat for the isbn.
     *
     * @param isbn the isbn to search for.
     * @return the json response body.
     * @throws IOException when there's a problem.
     */
    private String queryWorldCat(String isbn) throws IOException {

        var token = getSearchToken(isbn);

        var connection = (HttpURLConnection) new URL(String.format(WORLDCAT_REQUEST, isbn)).openConnection();
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("Referer", String.format("http://www.worldcat.org/search?q=%s", isbn));
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 ");
        connection.setRequestProperty("x-oclc-tkn", token);
        connection.setRequestProperty("Cookie",
            StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));

        var status = connection.getResponseCode();

        try (var br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            var sb = new TextStringBuilder();
            for (var line = br.readLine();
                 line != null;
                 line = br.readLine()) {
                sb.appendln(line);
            }
            return sb.toString();
        }
    }

    /**
     * Gets the search token from world cat.
     *
     * @param isbn the isbn to search.
     * @return the token to use in search request.
     * @throws IOException when there's a problem.
     */
    private String getSearchToken(String isbn) throws IOException {
        var connection = (HttpURLConnection) new URL(String.format(WC_TOKEN_REQUEST, wcBuild, isbn)).openConnection();
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("Referer", "http://www.worldcat.org/");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 ");
        connection.setRequestProperty("Cookie",
            StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));

        var status = connection.getResponseCode();

        try (var br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            var sb = new TextStringBuilder();
            var c = new char[400];
            for (var read = br.read(c, 0, c.length);
                 read > 0;
                 read = br.read(c, 0, c.length)) {
                sb.append(c);
                var match = WC_TOKEN_PATTERN.matcher(sb);
                if (match.find()) {
                    return match.group(1);
                }
            }
        }

        throw new IOException("TOKEN NOT FOUND!!!");
    }

    private void initializeCookies() throws IOException, URISyntaxException, InterruptedException {

        SetInitialCookies();
        SetLocationCookie();

        firstTime = false;
    }

    /**
     * Fetches the intial cookies from wc, and sets a generated source id.
     * Creates the cookie manager.
     * Also finds the build id.
     *
     * @throws IOException
     */
    private void SetInitialCookies() throws IOException {
        var response = Jsoup.connect("https://worldcat.org")
            .method(Connection.Method.GET)
            .execute();

        cookieManager = new CookieManager();
        response
            .cookies()
            .forEach((c, v) -> cookieManager.getCookieStore().add(null, new HttpCookie(c, v)));
        cookieManager.getCookieStore().add(null, new HttpCookie("wc_source_request_id", UUID.randomUUID().toString()));

        try (var br = new BufferedReader(new InputStreamReader(response.bodyStream()))) {
            for (var line = br.readLine(); line != null; line = br.readLine()) {
                var match = WC_BUILD_PATTERN.matcher(line);
                if (match.find()) {
                    wcBuild = match.group(1);
                    return;
                }
            }
        }
    }

    /**
     * Fetches and sets the location cookie.
     *
     * @throws IOException
     */
    private void SetLocationCookie() throws IOException {
        var connection = (HttpsURLConnection) new URL(WC_LOCATION_REQUEST).openConnection();
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("Referer", "https://www.worldcat.org/");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 ");
        connection.setRequestProperty("Cookie",
            StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));

        var status = connection.getResponseCode();

        try (var br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            var location = br.readLine();
            cookieManager.getCookieStore().add(null, new HttpCookie("wc_location", location));
        }

        connection.disconnect();
    }

    /**
     * Gets the oclc from the world cat json root node.
     *
     * @param root the world cat JsonNode root.
     * @return the oclc
     * @throws IOException when the json doesn't contain any book records.
     */
    private String findOclc(JsonNode root) throws IOException {
        var numOfRecords = root.path("numberOfRecords").asInt();
        if (numOfRecords < 1) {
            throw new IOException("ISBN not in world cat");
        }

        var firstRecord = root.path("briefRecords").get(0);
        var oclcNode = firstRecord.path("oclcNumber");

        return oclcNode.asText();
    }

    //
//    {
//        "numberOfRecords": 1,
//        "briefRecords": [
//        {
//            "oclcNumber": "1344342536",
//            "isbns": [
//            "9788934951056",
//                "8934951052"
//      ],
//            "isbn13": "9788934951056",
//            "title": "책 먹는 여우의 여름 이야기",
//            "creator": "Franziska Biermann",
//            "contributors": [
//            {
//                "firstName": {
//                "text": "프란치스카 비어만 글·그림 ; 송순섭 옮김",
//                    "romanizedText": "P'ŭranch'isŭk'a Piŏman kŭl · kŭrim ; Song Sun-sŏp omkim",
//                    "languageCode": "KO",
//                    "textDirection": "LTR"
//            },
//                "isPrimary": false,
//                "fromStatementOfResponsibility": true
//            },
//            {
//                "firstName": {
//                "text": "Franziska"
    //            },
//                "secondName": {
    //                "text": "Biermann"
    //            },
//                "isPrimary": true,
//                "relatorCodes": [
    //                "aut",
    //                    "ill"
    //          ]
//            },
//            {
//                "firstName": {
//                "text": "송 순섭",
//                    "romanizedText": "Sun-sŏp",
//                    "languageCode": "KO",
//                    "textDirection": "LTR"
//            },
//                "secondName": {
//                "romanizedText": "Song",
//                    "languageCode": "KO",
//                    "textDirection": "LTR"
//            },
//                "isPrimary": false,
//                "relatorCodes": [
//                "trl"
//          ]
//            }
//      ],
//            "publicationDate": "2022",
//            "catalogingLanguage": "kor",
//            "generalFormat": "Book",
//            "specificFormat": "PrintBook",
//            "edition": "1-p'an",
//            "totalEditions": 1,
//            "publisher": "주니어 김영사",
//            "publicationPlace": "Kyŏnggi-do P'aju-si",
//            "digitalObjectInfo": null,
//            "subjects": [
//            "Foxes Juvenile fiction",
//                "Authors Juvenile fiction",
//                "Summer Juvenile fiction"
//      ],
//            "publication": null,
//            "summaries": [
//            "\"‘책 먹는 여우’의 두 번째 계절 모험 이야기. 재미난 이야기를 찾기 위해 도착한 섬에서 펼쳐진 뜻밖의 보물 같은 이야기!  해적들은 이졸라 아그네스섬에 무엇을 숨겨 놓았을까? ‘여우 아저씨’의 작가 수첩을 꽉 채운 신나는 여름 모험!\" -- Yes24.com"
//      ],
//            "summary": "\"‘책 먹는 여우’의 두 번째 계절 모험 이야기. 재미난 이야기를 찾기 위해 도착한 섬에서 펼쳐진 뜻밖의 보물 같은 이야기!  해적들은 이졸라 아그네스섬에 무엇을 숨겨 놓았을까? ‘여우 아저씨’의 작가 수첩을 꽉 채운 신나는 여름 모험!\" -- Yes24.com",
//            "abstract": null,
//            "otherFormats": null,
//            "peerReviewed": false,
//            "openAccessLink": null
//        }
//  ]
//    }
    private Book createBook(JsonNode root) throws IOException {
        var numOfRecords = root.path("numberOfRecords").asInt();
        if (numOfRecords < 1) {
            throw new IOException("OCLC not in world cat");
        }

        var firstRecord = root.path("briefRecords").get(0);
        var contributors = firstRecord.path("contributors"); // array

        var authors = StreamSupport.stream(contributors.spliterator(), false)
            .filter(c -> !c.has("fromStatementOfResponsibility"))
            .filter(c -> c.has("relatorCodes"))
            .filter(this::isAuthor)
            .filter(c -> c.has("firstName"))
            .collect(Collectors.toList());

        var book = new Book();
        var author = createAuthor(authors.get(0));
        book.setAuthor(author);

        if (authors.size() > 1) {
            var author2 = createAuthor(authors.get(1));
            book.setAuthor(author2);
        }

        return book;
    }

    private String createAuthor(JsonNode contributor) {
        var author = "";
        var firstName = contributor.path("firstName");
        if (firstName.has("text")) {
            author = firstName.path("text").asText();
        }

        if (contributor.has("secondName")) {
            var secondName = contributor.path("secondName");
            if(secondName.has("text")){
                author += ' ';
                author += secondName.path("text").asText();
            }
        }

        return author;
    }

    private boolean isAuthor(JsonNode contributor) {
        return StreamSupport.stream(contributor.path("realtorCodes").spliterator(), false)
            .map(JsonNode::toString)
            .anyMatch(s -> s.equals("aut"));
    }

    /**
     * Starts the cUrl process and returns it to get the input.
     * Doesn't use proxy if {@code proxy} is null.
     *
     * @param proxy the proxy to use.
     * @param isbn  the isbn to search for.
     * @return the started cUrl process.
     * @throws IOException if any problems arise when starting the process.
     */
    private Process getCurlProcess(String proxy, String isbn) throws IOException {
        var args = new ArrayList<String>(5);
        args.add("curl");
        args.add("--fail-with-body");

        if (proxy != null) {
            args.add("--proxy");
            args.add(proxy);
        }

        var link = String.format(WORLDCAT_REQUEST, isbn);
        args.add(link);

        var processBuilder = new ProcessBuilder(args);

        return processBuilder.start();
    }

    /**
     * Gets the oclc using the owi from classify.org
     *
     * @param owi the book's owi.
     * @return the oclc
     * @throws IOException if any problems when making a request to the 'classify' api.
     */
    private String GetOclc(String owi) throws IOException {
        var doc = GetClassifyDoc(String.format(OWI_REQUEST, owi));

        if (!IsResponseOK(doc)) {
            LOGGER.info(String.format("No OCLC found for %s", owi));
            return null;
        }

        var firstEdition = doc.selectFirst("edition");
        return firstEdition.attr("oclc");
    }

    /**
     * Gets the owi based on isbn from classify.org.
     *
     * @param isbn the book's isbn.
     * @return the owi
     * @throws IOException if any problems when making a request to the 'classify' api.
     */
    private String GetOwi(String isbn) throws IOException {
        var doc = GetClassifyDoc(String.format(ISBN_REQUEST, isbn));

        if (!IsResponseOK(doc)) {
            LOGGER.info(String.format("No OWI found for %s", isbn));
            return null;
        }

        var firstWork = doc.selectFirst("work");
        return firstWork.attr("owi");
    }

    /**
     * Gets a {@code Document} response from classify.org.
     *
     * @param url the url to connect to.
     * @return the response document.
     * @throws IOException if any problems when making a request to the 'classify' api.
     */
    private Document GetClassifyDoc(String url) throws IOException {
        var res = Jsoup.connect(url)
            .method(Connection.Method.GET)
            .cookies(classifyCookies).execute();

        classifyCookies.putAll(classifyCookies);

        return Jsoup.parse(res.body(), "http://classify.oclc.org", Parser.xmlParser());
    }


    /**
     * Gets whether the classify.org response successfully found works.
     *
     * @param doc the 'classify' xml response.
     * @return whether the request was successful.
     */
    private boolean IsResponseOK(Document doc) {
        var responseElement = doc.selectFirst("response");
        var responseCode = responseElement.attr("code");

        return SUCCESS_CODES.contains(responseCode);
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        throw new NotImplementedException("Not supported");
    }

    @Override
    public Book fillInAllDetails(Book book) {
        throw new NotImplementedException("Not supported");
    }

    @Override
    public String BookPagePrefix() {
        throw new NotImplementedException("Not supported");
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        throw new NotImplementedException("Not supported");
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        throw new NotImplementedException("Not supported");
    }

    @Override
    public void checkInventoryAndOclc(Book result) {
        throw new NotImplementedException("Not supported");
    }
}
