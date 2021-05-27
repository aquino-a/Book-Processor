/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;


import com.aquino.webParser.model.Book;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

/**
 *
 * @author alex
 */
public class ExcelWriter {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final XSSFWorkbook workbook;
    private int startRow;
    //private final File saveFile;
    private final XSSFSheet sheet;
    
    public ExcelWriter(XSSFWorkbook workbook) {
        this.workbook = workbook;
        //this.saveFile = saveFile;
        this.sheet = workbook.getSheetAt(0);
        retrieveStartRow();
    }
    private void retrieveStartRow() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            
            if ( sheet.getRow(i) == null || 
                    sheet.getRow(i).getCell(1) == null) {
                LOGGER.info("Row {0} is null.", i);
                startRow = i;
                return;
            }
        }
    }
    private void writeEntry(int rowNumber, Book book) {
        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCellStyle isbnNumberFormat = workbook.createCellStyle();
        isbnNumberFormat.setDataFormat(workbook.createDataFormat().getFormat("#####"));
        XSSFCellStyle yellowBackground = workbook.createCellStyle();
        yellowBackground.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        if(book.getIsbn() == -1) {
            row.createCell(0).setCellValue(book.getIsbnString());
            row.createCell(1).setCellValue(book.getIsbnString());
        } else {
            row.createCell(0).setCellValue(book.getIsbn());
            row.getCell(0).setCellStyle(isbnNumberFormat);
            row.createCell(1).setCellValue(book.getIsbn());
            row.getCell(1).setCellStyle(isbnNumberFormat);
        }
        row.createCell(2).setHyperlink(createHyperLink(book.getBookPageUrl()));
        row.getCell(2).setCellValue("Book page");
        if(book.getOclc() != -1)
            row.createCell(3).setCellValue(book.getOclc());
        if(!book.getEnglishTitle().equals(""))
            row.createCell(4).setCellValue(book.getEnglishTitle());
        row.createCell(6).setCellValue(book.getRomanizedTitle());
        row.createCell(7).setCellValue(book.getTitle());

        row.createCell(9).setCellValue(book.getAuthorId());
        row.createCell(10).setCellValue(book.getAuthor());
        row.createCell(11).setCellValue(book.getAuthorBooks());

        row.createCell(12).setCellValue(book.getAuthor2Id());
        row.createCell(13).setCellValue(book.getAuthor2());
        row.createCell(14).setCellValue(book.getAuthor2Books());

        row.createCell(15).setCellValue(book.getPublisherId());
        row.createCell(16).setCellValue(book.getPublisher());
        row.createCell(17).setCellValue(book.getPublisherBooks());

        row.createCell(18).setCellValue(book.getCategory());
        row.createCell(19).setCellValue(book.getCategory2());
        row.createCell(21).setCellValue(book.getVendorName());
        row.createCell(22).setCellValue(book.getLanguageCode());
        if(!book.getEnglishTitle().equals("")){
            row.createCell(19).setCellValue(book.getAuthorOriginal());
            row.createCell(24).setCellValue(book.getAuthorOriginal());
        }
        row.createCell(25).setCellValue(book.getPublishDateFormatted());
        row.createCell(26).setCellValue(book.getCurrencyType());
        row.createCell(27).setCellValue(book.getOriginalPriceNumber());
//        row.getCell(21).setCellStyle(yellowBackground);
        row.createCell(28).setCellValue(book.getImageURL());
        row.getCell(28).setCellStyle(workbook.createCellStyle());
        if(!book.getTranslator().equals(""))
            row.createCell(29).setCellValue(book.getTranslator());
        row.createCell(30).setHyperlink(createHyperLink(book.getImageURL()));
        row.getCell(30).setCellValue("Image");
        row.createCell(32).setCellValue(book.getBookSizeFormatted());
        row.createCell(33).setCellValue(book.getType());
        row.createCell(34).setCellValue(book.getPages());
        row.createCell(37).setCellValue(book.getWeight());
        row.createCell(38).setCellValue("Books");
        row.createCell(40).setCellValue(0);
        row.createCell(41).setCellValue(1);
        row.createCell(43).setCellValue(1);
        row.createCell(44).setCellValue(0);
        book.getMiscellaneous().forEach(ei -> {
            if(ei.getColumnNumber() <= 44){
                return;
            }
            switch (ei.getType()){
                case HyperLink: {
                    row.createCell(ei.getColumnNumber()).setHyperlink(createHyperLink(ei.getValue()));
                    row.getCell(ei.getColumnNumber()).setCellValue(ei.getName());
                }
                break;
                default: break;
            }
        });
    }

    private boolean IsStringAllNum(String str) {
        for (char c: str.toCharArray()) {
            if(!Character.isDigit(c))
                return false;
        }
        return true;
    }

    private Hyperlink createHyperLink(String imageURL) {
        if(imageURL == null || imageURL.equals(""))
            return null;
        XSSFHyperlink result = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
        result.setAddress(imageURL);
        return result;
    }

    public void saveFile(File saveFile) {
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            LOGGER.info("Saving file");
            workbook.write(fos);
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void writeDemo(Book oldBook) {
        writeEntry(startRow, oldBook);
    }
    private void writeEntries(List<Book> books) {
        int i = startRow;
        for (Book book : books) {
            writeEntry(i++, book);
        }
    }
    public void writeBooks(List<Book> books) {
        retrieveStartRow();
        writeEntries(books);
    }
}
