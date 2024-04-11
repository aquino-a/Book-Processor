package com.aquino.webParser.speed;

import java.io.IOException;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

public interface SpeedDownloader {

    void download(AladinCategory category) throws IOException;

}