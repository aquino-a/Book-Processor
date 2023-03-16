package com.aquino.webParser.chatgpt;

/**
 *
 * @author alex
 */
public interface ChatGptService {

    /**
     * Takes text that describes a book and returns a summary of the book in English.
     * 
     * @param descriptionText
     * @return summary in English.
     */
    public String getSummary(String descriptionText);
}
