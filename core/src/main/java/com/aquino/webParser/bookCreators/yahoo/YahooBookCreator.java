package com.aquino.webParser.bookCreators.yahoo;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YahooBookCreator implements BookCreator {

    private static final Logger LOGGER = LogManager.getLogger();
    // private static final String SEARCH_URL_FORMAT =
    // "https://paypaymall.yahoo.co.jp/search?p=%s&cid=&brandid=&kspec=&b=1";
    // private static final String SEARCH_URL_FORMAT =
    // "https://search.yahoo.co.jp/commerce/catalog/detail?p=%s&fr=top_ga1_sa&ml=prop%%3Aec_ctlog%%3Bq%%3A%s%%3Bctlgid%%3A12455374027%%3Bbffstype%%3Aec_pdlist%%3Bfrtype%%3Aec_list-1-pdlist_ec_list-ttl%%3Bdestprf%%3A13%%3Bmtids%%3A1962_ctrl%%2C1882_5%%2C66_2%%2C2008_3%%2C10167_ctrl%%2C1948_2%%2C104_1%%2C2005_1%%3Bcvpk%%3A1962_1%%2C2008_2%%2C1948_3%%2C104_1%%2C2005_2";
    private static final String BOOK_URL_FORMAT = "https://store.shopping.yahoo.co.jp/honyaclubbook/%s.html";

    private static final Pattern AUTHOR_REGEX = Pattern.compile("著者名：([^<]+)");
    private static final Pattern KATAGANA_REGEX = Pattern.compile("キーワード：([^<]+)");
    private static final String COMBINE_FORMAT = "%s / %s";

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(BOOK_URL_FORMAT, isbn));
        if (doc == null)
            throw new IOException(String.format("Yahoo Book Page wasn't loaded: %s", isbn));
        Book book = new Book();
        book.setBookPageUrl(doc.location());
        return book;
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        return null;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        try {
            var bookDoc = Jsoup.connect(book.getBookPageUrl())
                    .get();

            var p = bookDoc.select("div.styles_itemInfoDetail__Dt_sv [data-testid='information'] p")
                    .getFirst();
            var pBody = p.outerHtml();

            var hanjaAuthors = getHanjaAuthors(pBody);
            var kataganaAuthors = getKataAuthors(pBody);

            if (hanjaAuthors.length > 0) {
                book.setAuthorBooks(getAuthorName(hanjaAuthors, kataganaAuthors, 0));
            }

            if (hanjaAuthors.length > 1) {
                if (book.getAuthor2Id() < 0) {
                    book.setAuthor2Books(getAuthorName(hanjaAuthors, kataganaAuthors, 1));
                } else {
                    LOGGER.warn("Author 2 id is greater than -1. Not setting katagana for 2nd author.");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Problem getting Yahoo katagana.", e);
        }

        return book;
    }

    private String getAuthorName(String[] name1, String[] name2, int index) {
        var name = name1[index];
        if (index < name2.length) {
            name = String.format(COMBINE_FORMAT, name, name2[index]);
        }

        return name;
    }

    private String[] getKataAuthors(String pBody) {
        Matcher km = KATAGANA_REGEX.matcher(pBody);
        if (km.find()) {
            return km.group(1).split("[、,]");
            // [アフレタ マチ, イルストゥラホ, マリアホ, スズキ, サオリ]
        }

        LOGGER.warn("No katagana found for authors: {}", pBody);
        return new String[0];
    }

    private String[] getHanjaAuthors(String pBody) {
        var am = AUTHOR_REGEX.matcher(pBody);
        if (am.find()) {
            return am.group(1).split("、");
            // [マリアホ・イルストゥラホ, 鈴木沙織]
        }

        LOGGER.warn("No Hanja authors found: {}", pBody);
        return new String[0];
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
