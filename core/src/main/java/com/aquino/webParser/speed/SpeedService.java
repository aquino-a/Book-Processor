package com.aquino.webParser.speed;

import java.util.concurrent.CompletableFuture;
import java.awt.image.BufferedImage;

public interface SpeedService {

    CompletableFuture<BufferedImage> GetImage(long id);
    CompletableFuture<String> GetDescription(long id);
    CompletableFuture<String> GetAwards(long id);

}
