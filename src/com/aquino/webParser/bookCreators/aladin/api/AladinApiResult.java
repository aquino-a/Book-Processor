package com.aquino.webParser.bookCreators.aladin.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinApiResult {
    private final List<AladinBookDTO> results;

    public AladinApiResult(
            @JsonProperty("item") List<AladinBookDTO> results
    ) {
        this.results = unmodifiableList(new ArrayList<AladinBookDTO>(results));
    }

    @JsonProperty("item")
    public List<AladinBookDTO> getResults() {
        return results;
    }

    public AladinBookDTO getResult() {
        if(results.size() > 0)
            return results.get(0);
        else return null;
    }
}
