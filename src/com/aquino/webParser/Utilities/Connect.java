package com.aquino.webParser.Utilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author alex
 */
public class Connect {
    //must check for null
    public static Document connectToURL(String url) {
        try {
//            System.out.println(url);
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println(e.toString());
            return null;
        }
        
    }
    // must check for null
    private static XSSFWorkbook getWorkbook(InputStream file ) {
        try {
            return new XSSFWorkbook(OPCPackage.open(file));
        } catch (IOException | InvalidFormatException e) {
            return null;
        }
    }
//    private static XSSFWorkbook getWorkbookStream(InputStream file) {
//        try {
//            return new XSSFWorkbook(OPCPackage.open(file));
//        } catch (Exception e) {
//            return null;
//        }
//    }
       
    public static XSSFWorkbook openExistingWorkbook(File file) 
            throws NullPointerException, FileNotFoundException {
        //return getWorkbook(file);
        return getWorkbook(new FileInputStream(file));
    }
    public static XSSFWorkbook newWorkbookFromTemplate() {
        String path = "/com/aquino/webParser/resources/template/TEMPLATE_FOR_JWP.xlsx";
        try {
            return getWorkbook(Connect.class.getResourceAsStream(path));
        } catch (Exception e) {
            return null;
        } 
    }
}


