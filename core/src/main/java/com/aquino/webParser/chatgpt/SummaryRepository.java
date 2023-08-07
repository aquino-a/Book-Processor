/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aquino.webParser.chatgpt;

/**
 *
 * @author alex
 */
public interface SummaryRepository {
    public String get(String isbn);

    public void save(String isbn, String summary);

    public String getTitle(String isbn);

    public void saveTitle(String isbn, String title);

    /**
     * Gets the 3 categories joined by a ',' based on isbn.
     * 
     * @param isbn
     * @return
     */
    public String getCategory(String isbn);

    public void saveCategory(String isbn, String combinedCodes);

    public String getNativeSummary(String isbn);

    public void saveNativeSummary(String isbn, String nativeSummary);
}
