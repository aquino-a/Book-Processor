package com.aquino.webParser.model;

public enum Language {
    Japanese("3000"), Korean("4000");

    public final String LanguageCode;

    private Language(String langCode){
        this.LanguageCode = langCode;
    }
}
