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
}
