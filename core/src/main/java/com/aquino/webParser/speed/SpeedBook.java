package com.aquino.webParser.speed;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "Book")
public class SpeedBook {
    
    @Id
    private long speedBookId;

    private LocalDate incompletePublishDate;
    private LocalDate publishDate;
    private String itemUrl;

    public long speedBookId() {
        return speedBookId;
    }

    public void setSpeedBookId(long speedBookId) {
        this.speedBookId = speedBookId;
    }

    public LocalDate incompletePublishDate() {
        return incompletePublishDate;
    }

    public void setIncompletePublishDate(LocalDate incompletePublishDate) {
        this.incompletePublishDate = incompletePublishDate;
    }

    public LocalDate publishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String itemUrl() {
        return itemUrl;
    }
    
    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }
}
