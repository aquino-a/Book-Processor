package com.aquino.webParser.oclc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class OclcServiceImpl implements OclcService {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ProxyList PROXY_LIST = new ProxyList();

    private static final String ISBN_REQUEST = "http://classify.oclc.org/classify2/Classify?isbn=%s&summary=true";
    private static final String OWI_REQUEST = "http://classify.oclc.org/classify2/Classify?owi=%s";
    private static final String WORLDCAT_REQUEST = "\"http://www.worldcat.org/api/search?q=%s&audience=&author=" +
        "&content=&datePublished=&inLanguage=&itemSubType=&itemType=&limit=10&offset=1&openAccess=&orderBy=library" +
        "&peerReviewed=&topic=&heldByInstitutionID=&preferredLanguage=eng&relevanceByGeoCoordinates=true" +
        "&lat=35.5625&lon=129.1235\"";

    private static final Set<String> SUCCESS_CODES = Set.of("0", "2", "4");

    private final Map<String, String> classifyCookies = new HashMap<>();

    @Override
    public long findOclc(String isbn) {
        try {
            var owi = GetOwi(isbn);

            var oclc = owi == null
                ? GetWorldCatOclc(isbn)
                : GetOclc(owi);

            return Long.parseLong(oclc);
        } catch (IOException | InterruptedException e) {
            LOGGER.info(String.format("Couldn't get OCLC for %s", isbn));
            LOGGER.info(e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Tries to get the oclc from worldcat.org.
     *
     * @param isbn the book's isbn.
     * @return the oclc if found
     */
    private String GetWorldCatOclc(String isbn) throws IOException, InterruptedException {
        while (true) {
            var proxy = getProxy();
            var process = getCurlProcess(proxy, isbn);

            try (var is = process.getInputStream()) {
                JsonNode root = null;
                try {
                    root = OBJECT_MAPPER.readTree(is);
                } catch (JsonParseException e) {
                    LOGGER.info("Request body not valid");
                    PROXY_LIST.removeProxy(proxy);
                    continue;
                }

                process.waitFor();
                if (process.exitValue() > 0) {
                    PROXY_LIST.removeProxy(proxy);
                    LOGGER.info("Request to world cat failed");
                    continue;
                }

                return findOclc(root);
            }
        }
    }

    /**
     * Gets a proxy from {@code PROXY_LIST}.
     * Returns null if any IOException is thrown.
     *
     * @return a proxy
     */
    private String getProxy() {
        try {
            return PROXY_LIST.getProxy();
        } catch (IOException e) {
            LOGGER.info("No proxy found, trying without proxy.");
            return null;
        }
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

    /**
     * Starts the cUrl process and returns it to get the input.
     * Doesn't use proxy if {@code proxy} is null.
     *
     * @param proxy the proxy to use.
     * @param isbn the isbn to search for.
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
}
