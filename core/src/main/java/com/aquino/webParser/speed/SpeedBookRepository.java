package com.aquino.webParser.speed;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

/**
 * SpeedBookRepository
 */
public interface SpeedBookRepository {

    // todo get by category, with date cursor
    // save book
    // update is used 
    // 
    CompletableFuture<Stream<SpeedBook>> getByCategory(AladinCategory category);
    void save(SpeedBook book);
    void update(SpeedBook book);
}