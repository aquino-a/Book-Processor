package com.aquino.webParser.bookCreators.aladin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinPacking {
    private final int sizeHeight, sizeWidth;
    private String bookType;

    @JsonCreator
    public AladinPacking(
            @JsonProperty("sizeHeight") int sizeHeight,
            @JsonProperty("sizeWidth") int sizeWidth

    ) {
        this.sizeHeight = sizeHeight;
        this.sizeWidth = sizeWidth;
    }

    public int getSizeHeight() {
        return sizeHeight;
    }

    public int getSizeWidth() {
        return sizeWidth;
    }

    public String getBookType() {
        return bookType;
    }

    @JsonProperty("styleDesc")
    public void setBookType(String bookTypeSource) {
        if(bookTypeSource.contains("반"))
            bookType = "PB";
        else bookType = "HC";
    }
}
