package com.aquino.webParser.bookCreators.aladin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookInfo {
    private final List<AladinAuthor> authors;
    private final List<String> imageUrls;
    private final int pages;
    private String originalTitle;

    @JsonCreator
    public AladinBookInfo(
            @JsonProperty("authors") List<AladinAuthor> authors,
            @JsonProperty("letslookimg") List<String> imageUrls,
            @JsonProperty("itemPage") int pages
    ) {
        this.authors = unmodifiableList(new ArrayList<AladinAuthor>(authors));
        this.imageUrls = unmodifiableList(new ArrayList<String>(imageUrls));
        this.pages = pages;
    }

    @JsonProperty("authors")
    public List<AladinAuthor> getAuthors() {
        return authors;
    }

    @JsonProperty("imageUrls")
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    //TODO remove year
    @JsonProperty("originalTitle")
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

}
