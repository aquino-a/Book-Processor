/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.swing;

import com.aquino.webParser.ProcessorFactoryImpl;
import com.aquino.webParser.bookCreators.BookCreatorType;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 *
 * @author alex
 */
public class JWPdemo {
    public static void main(String[] args) throws URISyntaxException {

        ProcessorFactoryImpl processorFactory = new ProcessorFactoryImpl();
        java.awt.EventQueue.invokeLater(() ->
        {
            try {
                (new JWPUserInterface(processorFactory,processorFactory.CreateBookCreator(BookCreatorType.AladinApi))).createAndShowGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
