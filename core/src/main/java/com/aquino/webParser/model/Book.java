package com.aquino.webParser.model;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private String title, publishDate, originalPrice, bookSize,
            cover, publishDateFormatted, bookSizeFormatted,
            imageURL, author, englishTitle, translator,
            publisher, author2, isbnString, description, category, category2, category3, authorOriginal,
            locationUrl, kIsbn, bookPageUrl, languageCode, currencyType, romanizedTitle = "",
            authorBooks, author2Books, publisherBooks, summary, translatedTitle;
    private long isbn, oclc;
    private boolean titleExists;

    private int pages;
    private int authorId = -1;
    private int author2Id = -1;
    private int publisherId = -1;
    private int weight;

    private int originalPriceNumber;
    private double originalPriceFormatted;
    private String vendorName = "";

    private List<ExtraInfo> miscellaneous;

    public boolean isTitleExists() {
        return titleExists;
    }

    public void setTitleExists(boolean titleExists) {
        this.titleExists = titleExists;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getBookSize() {
        return bookSize;
    }

    public void setBookSize(String bookSize) {
        this.bookSize = bookSize;
    }

    public String getType() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPublishDateFormatted() {
        return publishDateFormatted;
    }

    public void setPublishDateFormatted(String publishDateFormatted) {
        this.publishDateFormatted = publishDateFormatted;
    }

    public String getBookSizeFormatted() {
        return bookSizeFormatted;
    }

    public void setBookSizeFormatted(String bookSizeFormatted) {
        this.bookSizeFormatted = bookSizeFormatted;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

    public void setEnglishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthor2() {
        return author2;
    }

    public void setAuthor2(String author2) {
        this.author2 = author2;
    }

    public String getIsbnString() {
        return isbnString;
    }

    public void setIsbnString(String isbnString) {
        this.isbnString = isbnString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthorOriginal() {
        return authorOriginal;
    }

    public void setAuthorOriginal(String authorOriginal) {
        this.authorOriginal = authorOriginal;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getkIsbn() {
        return kIsbn;
    }

    public void setkIsbn(String kIsbn) {
        this.kIsbn = kIsbn;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public long getOclc() {
        return oclc;
    }

    public void setOclc(long oclc) {
        this.oclc = oclc;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getOriginalPriceFormatted() {
        return originalPriceFormatted;
    }

    public void setOriginalPriceFormatted(double originalPriceFormatted) {
        this.originalPriceFormatted = originalPriceFormatted;
    }

    public int getOriginalPriceNumber() {
        return originalPriceNumber;
    }

    public void setOriginalPriceNumber(int parsePrice) {
        originalPriceNumber = parsePrice;
    }

    public String getBookPageUrl() {
        return bookPageUrl;
    }

    public void setBookPageUrl(String bookPageUrl) {
        this.bookPageUrl = bookPageUrl;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getRomanizedTitle() {
        return romanizedTitle;
    }

    public void setRomanizedTitle(String romanizedTitle) {
        this.romanizedTitle = romanizedTitle;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getAuthorBooks() {
        return authorBooks;
    }

    public void setAuthorBooks(String authorBooks) {
        this.authorBooks = authorBooks;
    }

    public String getPublisherBooks() {
        return publisherBooks;
    }

    public void setPublisherBooks(String publisherBooks) {
        this.publisherBooks = publisherBooks;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public String getAuthor2Books() {
        return author2Books;
    }

    public void setAuthor2Books(String author2Books) {
        this.author2Books = author2Books;
    }

    public int getAuthor2Id() {
        return author2Id;
    }

    public void setAuthor2Id(int author2Id) {
        this.author2Id = author2Id;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public List<ExtraInfo> getMiscellaneous() {
        if(miscellaneous == null)
            miscellaneous = new ArrayList<ExtraInfo>();
        return miscellaneous;
    }

    public void setMiscellaneous(List<ExtraInfo> miscellaneous) {
        this.miscellaneous = miscellaneous;
    }
    
    /**
     * Get the chatGpt summary
     *
     * @return the value of summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Set the chatGpt summary
     *
     * @param summary new value of summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }
}
