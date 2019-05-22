package com.aquino.webParser;

public class Book {

    private String title, publishDate, originalPrice, bookSize,
            cover, publishDateFormatted, bookSizeFormatted,
            imageURL, author, englishTitle, translator,
            publisher, author2, isbnString, description, category, authorOriginal,
            locationUrl, kIsbn, bookPageUrl, languageCode, currencyType ;
    private long isbn, oclc;
    private boolean titleExists;

    private int pages;
    private int weight;

    private int originalPriceNumber;
    private double originalPriceFormatted;


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
}
