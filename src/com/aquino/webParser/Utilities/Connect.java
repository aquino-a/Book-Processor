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
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author alex
 */
public class Connect {

    private static final String oclcRequest ="https://www.worldcat.org/isbn/%s";

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
    
    public static Document connectToURLwithHeaders(String url, Map<String,String> headers) {
        try {
            return Jsoup.connect(url).headers(headers).get();
        } catch (IOException e) {
            System.out.println(e.toString());
            return null;
        }
        
    }

    public static String readLocationHeader(String isbn){

        try {
            Connection.Response res = Jsoup.connect(String.format(oclcRequest, isbn)).followRedirects(false).method(Connection.Method.HEAD).execute();
            if(res.hasHeader("location"))
                return res.header("location");
            else return "-1";
        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
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

    public static void main(String[] args) {
        boolean test1 = readLocationHeader("9791162540666").equals("-1");
        boolean test2 = readLocationHeader("9791162540640").equals("https://www.worldcat.org/title/aju-chak-un-supkwan-ui-him-choego-ui-pyonhwa-nun-ottoke-manduroji-nunga-atomic-habits/oclc/1090062642");
    }
}


