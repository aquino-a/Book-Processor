/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.*;

import com.aquino.webParser.bookCreators.BookCreatorType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 *
 * @author alex
 */
public class JWPdemo {
    public static void main(String[] args) throws URISyntaxException {
//        XSSFWorkbook wk = Connect.newWorkbookFromTemplate();
//        System.out.println(wk);
        
        
        
        
        ProcessorFactoryImpl processorFactory = new ProcessorFactoryImpl();
        java.awt.EventQueue.invokeLater(() ->
        {
            try {
                (new JWPUserInterface(processorFactory,processorFactory.CreateBookCreator(BookCreatorType.AladinApi))).createAndShowGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        
        
        
        
        
        
        
        
        
        
        
        
        
        
//        
    }
    
}
