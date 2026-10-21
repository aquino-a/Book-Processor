package com.aquino.webParser.bookCreators.yodobashi;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class YodobashiBookCreator implements BookCreator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SEARCH_URL_FORMAT = "https://www.yodobashi.com/?word=%s";

    private static final Pattern SIZE_1_REGEX = Pattern.compile("判型：(.+)");
    private static final Pattern SIZE_2_REGEX = Pattern.compile("縦：(.+)");

    private final OkHttpClient client;

    public YodobashiBookCreator(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = getDoc(String.format(SEARCH_URL_FORMAT, isbn));
        if (doc == null) {
            throw new IOException(String.format("Yodobashi search page wasn't loaded: %s", isbn));
        }

        var bookUrl = doc.selectFirst("[data-sku] > a")
                .absUrl("href");

        if (StringUtils.isBlank(bookUrl)) {
            throw new IOException(String.format("Book wasn't found on search page %s", isbn));
        }

        Book book = new Book();
        book.setBookPageUrl(bookUrl);
        return book;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        try {
            var bookDoc = getDoc(book.getBookPageUrl());

            book.setOriginalPrice(parsePrice(bookDoc));
            book.setDescription(parseDescription(bookDoc));
            book.setCategory(parseCategory(bookDoc));
            book.setBookSize(parseSize(bookDoc));

        } catch (Exception e) {
            LOGGER.warn("Problem getting Yododashi price, description and category.", e);
        }

        return book;
    }

    private String parseSize(Document doc) {
        var rows = doc.select("div.specBox td div")
                .stream()
                .map(Element::text)
                .toList();

        var size1 = rows.stream()
                .map(SIZE_1_REGEX::matcher)
                .filter(Matcher::find)
                .map(m -> m.group(1))
                .findFirst()
                .orElse(null);

        var size2 = rows.stream()
                .map(SIZE_2_REGEX::matcher)
                .filter(Matcher::find)
                .map(m -> m.group(1))
                .findFirst()
                .orElse(null);

        return String.format("%s, %s", size1, size2);
    }

    private String parseCategory(Document doc) {
        return doc.select("div.breadcrumbs.js_ctlg_ageconfirm_permit span[itemprop=name]")
                .stream()
                .map(Element::text)
                .collect(Collectors.joining(" > "));
    }

    private String parseDescription(Document doc) {
        return doc.select("div#productSet1Box")
                .getFirst()
                .text();
    }

    private String parsePrice(Document doc) {
        // <meta content="1650" itemprop="price"/>
        return doc.select("meta[itemprop=price]")
                .getFirst()
                .attr("content");
    }

    private Document getDoc(String url) throws IOException {
        var request = new Request.Builder()
                .url(url)
                .header("user-agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/152.0.0.0 Safari/537.36")
                .build();
        var response = this.client.newCall(request)
                .execute();

        return Jsoup.parse(response.body().byteStream(), null, url);
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
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
