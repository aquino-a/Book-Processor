package com.aquino.webParser.bookCreators.aladin;

import com.aquino.webParser.Book;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookDTO {

    private String title, pubDate, category, imageUrl;
    private long isbn;
    private final String description,publisher, kIsbn;
    private final int price;

    private final AladinBookInfo aladinBookInfo;

    private static final DateTimeFormatter sourceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @JsonCreator
    public AladinBookDTO(
            @JsonProperty("subInfo") AladinBookInfo aladinBookInfo,
            @JsonProperty("fullDescription") String description,
            @JsonProperty("priceStandard") int price,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("isbn") String kIsbn

    ) {
        this.aladinBookInfo = aladinBookInfo;
        this.description = description;
        this.price = price;
        this.publisher = publisher;
        this.kIsbn = kIsbn;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title){
        if(title.contains(" - "))
            this.title = title.substring(0, title.indexOf(" - "));
        else this.title = title;
    }

    @JsonProperty("bookinfo")
    public AladinBookInfo getBookInfo() {
        return aladinBookInfo;
    }

    public String getPubDate() {
        return pubDate;
    }

    @JsonProperty("pubDate")
    public void setPubDate(String pubDate) {
        this.pubDate = LocalDate.parse(pubDate,sourceFormat).format(targetFormat);
    }
    public String getCategory() {
        return category;
    }

    @JsonProperty("categoryName")
    public void setCategory(String categorySource) {
        String[] tokens = categorySource.split(">");
        if(tokens.length >= 2){
            category = tokens[1];
        }
        else category = "???";
    }
    @JsonProperty("isbn13")
    public long getIsbn() {
        return isbn;
    }

    @JsonProperty("isbn13")
    public void setIsbn(String isbn) {
        this.isbn = Long.parseLong(isbn);
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("priceStandard")
    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("cover")
    public void setImageUrl(String imageUrl) {
        StringBuilder sb = new StringBuilder(imageUrl);
        sb.replace(sb.indexOf("coversum"), sb.indexOf("coversum") + 8, "letslook");
        sb.setCharAt(sb.lastIndexOf("_") + 1, 'f');
        this.imageUrl = sb.toString();
    }

    public String getPublisher() {
        return publisher;
    }

    public String getkIsbn() {
        return kIsbn;
    }

    public Book asBook() {
        Book book = new Book();
        book.setCurrencyType("Won");
        book.setLanguageCode("KOR");
        book.setAuthorOriginal("");
        setAuthors(book);
        book.setEnglishTitle(getBookInfo().getOriginalTitle() == null ? getBookInfo().getOriginalTitle() : "");
        book.setPublisher(getPublisher());
        book.setTitle(getTitle());
        book.setWeight(getBookInfo().getPages() % 300 > 1 ? (getBookInfo().getPages() / 300) + 1 : getBookInfo().getPages() / 300);
        setBookSizeFormatted(book);
        book.setCover(getBookInfo().getPacking().getBookType());
        book.setDescription(getDescription());
        book.setImageURL(getImageUrl());
        book.setIsbn(getIsbn());
        book.setOriginalPriceNumber(getPrice());
        book.setPages(getBookInfo().getPages());
        book.setPublishDateFormatted(getPubDate());
        book.setCategory(getCategory());
        book.setkIsbn(getkIsbn());
        return book;
    }

    private void setBookSizeFormatted(Book book) {
        double first = (double) getBookInfo().getPacking().getSizeHeight() / 10;
        double second = (double) getBookInfo().getPacking().getSizeWidth()/ 10;
        if (first % 1 != 0 && second % 1 != 0) {
            book.setBookSizeFormatted(first + " x " + second);
        } else if (first % 1 == 0 && second % 1 == 0) {
            book.setBookSizeFormatted((int) first + " x " + (int) second);
        } else if (first % 1 == 0) {
            book.setBookSizeFormatted((int) first + " x " + second);
        } else if (second % 1 == 0) {
            book.setBookSizeFormatted(first + " x " + (int) second);
        }
    }

    private void setAuthors(Book book) {
        List<AladinAuthor> authors = getBookInfo().getAuthors().stream().filter(a -> a.getAuthorType().equals("author")).collect(Collectors.toList());
        if(authors.size() > 0 && authors.size() < 3){
            book.setAuthor(authors.get(0).getName());
            if(authors.size() == 2)
                book.setAuthor2(authors.get(1).getName());
            else book.setAuthor2("");
        } else if(authors.size() >= 3){
            book.setAuthor("1494");
            book.setAuthor2("");
        } else throw new UnsupportedOperationException();
        List<AladinAuthor> translators =  getBookInfo().getAuthors().stream().filter(a -> a.getAuthorType().equals("translator")).collect(Collectors.toList());
        if(translators.size() > 0){
            for (AladinAuthor a : translators){
                if(book.getTranslator() == null)
                    book.setTranslator(a.getName());
                else book.setTranslator(book.getTranslator() + " & " + a.getName());
            }
        } else book.setTranslator("");
    }
}
