package com.aquino.webParser.bookCreators.aladin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinAuthor {
    private final String authorType;
    private final String name;


    //TODO enum for authorType
    @JsonCreator
    public AladinAuthor(@JsonProperty("authorType") String authorType,@JsonProperty("name") String name) {
        this.authorType = authorType;
        this.name = name;
    }
}
