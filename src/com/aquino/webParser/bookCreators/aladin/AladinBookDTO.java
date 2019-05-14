package com.aquino.webParser.bookCreators.aladin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AladinBookDTO {

    private final String title;

    private final AladinBookInfo aladinBookInfo;

    @JsonCreator
    public AladinBookDTO(
            @JsonProperty("title")    String   title,
            @JsonProperty("bookinfo") AladinBookInfo aladinBookInfo
    ) {
        this.title    = title;
        this.aladinBookInfo = aladinBookInfo;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("bookinfo")
    public AladinBookInfo getBookInfo() {
        return aladinBookInfo;
    }
}
