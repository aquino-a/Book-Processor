package com.aquino.webParser.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "REVIEW_POSITION")
public class ReviewPosition {
    @Id
    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "ISBN")
    private String isbn;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}

