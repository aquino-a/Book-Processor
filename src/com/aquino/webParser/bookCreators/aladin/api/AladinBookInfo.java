package com.aquino.webParser.bookCreators.aladin.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookInfo {
    private final List<AladinAuthor> authors;
    private final int pages;
    private final AladinPacking packing;
    private String originalTitle;

    @JsonCreator
    public AladinBookInfo(
            @JsonProperty("authors") List<AladinAuthor> authors,
            @JsonProperty("itemPage") int pages,
            @JsonProperty("packing") AladinPacking packing
    ) {
        this.authors = unmodifiableList(new ArrayList<AladinAuthor>(authors));
        this.pages = pages;
        this.packing = packing;
    }

    @JsonProperty("authors")
    public List<AladinAuthor> getAuthors() {
        return authors;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    //TODO remove year
    @JsonProperty("originalTitle")
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public AladinPacking getPacking() {
        return packing;
    }

    public int getPages() {
        return pages;
    }
}
