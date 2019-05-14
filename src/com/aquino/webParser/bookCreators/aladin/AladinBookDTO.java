package com.aquino.webParser.bookCreators.aladin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookDTO {

    private String title, pubDate, category;
    private long isbn;
    private final String description;
    private final int price;

    private final AladinBookInfo aladinBookInfo;

    private static final DateTimeFormatter sourceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @JsonCreator
    public AladinBookDTO(
            @JsonProperty("bookinfo") AladinBookInfo aladinBookInfo,
            @JsonProperty("description") String description,
            @JsonProperty("priceStandard") int price
    ) {
        this.aladinBookInfo = aladinBookInfo;
        this.description = description;
        this.price = price;
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

    //TODO parse
    @JsonProperty("categoryName")
    public void setCategory(String category) {
        this.category = category;
    }

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
}
