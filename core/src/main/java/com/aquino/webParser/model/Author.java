package com.aquino.webParser.model;

import org.apache.commons.lang3.StringUtils;

public class Author {

    private Language language;
    private String englishFirstName;
    private String englishLastName;
    private String nativeFirstName;
    private String nativeLastName;
    private int id;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getEnglishFirstName() {
        return englishFirstName;
    }

    public void setEnglishFirstName(String englishFirstName) {
        this.englishFirstName = englishFirstName;
    }

    public String getEnglishLastName() {
        return englishLastName;
    }

    public void setEnglishLastName(String englishLastName) {
        this.englishLastName = englishLastName;
    }

    public String getNativeFirstName() {
        return nativeFirstName;
    }

    public void setNativeFirstName(String nativeFirstName) {
        this.nativeFirstName = nativeFirstName;
    }

    public String getNativeLastName() {
        return nativeLastName;
    }

    public void setNativeLastName(String nativeLastName) {
        this.nativeLastName = nativeLastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBlank(){
        return
            StringUtils.isBlank(nativeFirstName) &&
            StringUtils.isBlank(nativeLastName) &&
            StringUtils.isBlank(englishFirstName) &&
            StringUtils.isBlank(englishLastName);
    }
}
