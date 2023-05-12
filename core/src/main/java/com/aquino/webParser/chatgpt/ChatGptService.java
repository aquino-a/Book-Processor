package com.aquino.webParser.chatgpt;

import com.aquino.webParser.model.Book;

/**
 *
 * @author alex
 */
public interface ChatGptService {

    /**
     * Takes text that describes a book and returns a summary of the book in
     * English.
     * 
     * @param book
     * @return summary in English.
     */
    public String getSummary(Book book);

    /**
     * Translates book title into English.
     * 
     * @param book
     * @return translated title
     */
    public String getTitle(Book book);
}
