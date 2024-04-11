package com.aquino.webParser.speed;

import java.time.LocalDate;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "Book")
public class SpeedBook {

    @Id
    private long speedBookId;

    private LocalDate incompletePublishDate;
    private LocalDate publishDate;
    private String itemUrl;
    private String imageUrl;
    private int price;
    private String title;
    private int rank;
    private AladinCategory category;
    
    public AladinCategory category() {
        return category;
    }

    public void category(AladinCategory category) {
        this.category = category;
    }

    public String title() {
        return title;
    }

    public void title(String title) {
        this.title = title;
    }

    public int rank() {
        return rank;
    }

    public void rank(int rank) {
        this.rank = rank;
    }

    public int price() {
        return price;
    }

    public void price(int price) {
        this.price = price;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public void imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long speedBookId() {
        return speedBookId;
    }

    public void speedBookId(long speedBookId) {
        this.speedBookId = speedBookId;
    }

    public LocalDate incompletePublishDate() {
        return incompletePublishDate;
    }

    public void incompletePublishDate(LocalDate incompletePublishDate) {
        this.incompletePublishDate = incompletePublishDate;
    }

    public LocalDate publishDate() {
        return publishDate;
    }

    public void publishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String itemUrl() {
        return itemUrl;
    }

    public void itemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }
}
