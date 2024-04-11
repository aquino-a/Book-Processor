package com.aquino.webParser.speed;

import java.util.stream.Stream;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

public class AladinScraper {

    private final SpeedDownloader downloader;

    public AladinScraper(SpeedDownloader downloader) {
        this.downloader = downloader;
    }

    public void ScrapeParallel() {
        Stream.of(AladinCategory.values())
                .parallel()
                .forEach(this::download);
    }

    public void Scrape() {
        Stream.of(AladinCategory.values())
                .forEach(this::download);
    }

    private void download(AladinCategory category) {
        try {
            downloader.download(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
