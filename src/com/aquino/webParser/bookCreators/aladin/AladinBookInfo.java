package com.aquino.webParser.bookCreators.aladin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class AladinBookInfo {
    private final List<AladinAuthor> authors;

    @JsonCreator
    public AladinBookInfo(
            @JsonProperty("authors") List<AladinAuthor> authors
    ) {
        this.authors = unmodifiableList(new ArrayList<AladinAuthor>(authors));
    }

    @JsonProperty("authors")
    public List<AladinAuthor> getAuthors() {
        return authors;
    }
}
