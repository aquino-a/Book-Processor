package com.aquino.webParser.bookCreators.aladin.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinAuthor {
    private final String authorType;
    private final String name;


    //TODO enum for authorType
    @JsonCreator
    public AladinAuthor(@JsonProperty("authorType") String authorType,@JsonProperty("authorName") String name) {
        this.authorType = authorType;
        this.name = name;
    }

    public String getAuthorType() {
        return authorType;
    }

    public String getName() {
        return name;
    }
}
