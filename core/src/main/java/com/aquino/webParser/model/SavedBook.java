package com.aquino.webParser.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "SUMMARY")
public class SavedBook {
    @Id
    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "SUMMARY")
    private String summary;

    @Column(name = "TITLE")
    private String translatedTitle;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "KOREAN_DESCRIPTION")
    private String koreanDescription;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getKoreanDescription() {
        return koreanDescription;
    }

    public void setKoreanDescription(String koreanDescription) {
        this.koreanDescription = koreanDescription;
    }
}
