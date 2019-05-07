/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;


import com.aquino.webParser.romanization.Romanizer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

/**
 *
 * @author alex
 */
public class ExcelWriter {
    
    private static final Logger logger = Logger.getLogger(ExcelWriter.class.getName());
    
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
                logger.log(Level.INFO, "Row {0} is null.", i);
                startRow = i;
                return;
            }
        }
    }
    private void writeEntry(int rowNumber, Book book) {
        if(book.getDoc() == null) return;
        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCellStyle isbnNumberFormat = workbook.createCellStyle();
        isbnNumberFormat.setDataFormat(workbook.createDataFormat().getFormat("#####"));
        XSSFCellStyle yellowBackground = workbook.createCellStyle();
        yellowBackground.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        if(book.getISBN() == -1) {
            row.createCell(0).setCellValue(book.getIsbnString());
            row.createCell(1).setCellValue(book.getIsbnString());
        } else {
            row.createCell(0).setCellValue(book.getISBN());
            row.getCell(0).setCellStyle(isbnNumberFormat);
            row.createCell(1).setCellValue(book.getISBN());
            row.getCell(1).setCellStyle(isbnNumberFormat);
        }
        if(book.getOCLC() != -1)
            row.createCell(3).setCellValue(book.getOCLC());
        if(!book.getEnglishTitle().equals(""))
            row.createCell(4).setCellValue(book.getEnglishTitle());
        row.createCell(6).setCellValue(Romanizer.hangulToRoman(book.getTitle()));
        row.createCell(7).setCellValue(book.getTitle());
        if(Character.isDigit(book.getAuthor().charAt(0)))
            row.createCell(9).setCellValue(Integer.parseInt(book.getAuthor()));
        else row.createCell(9).setCellValue(book.getAuthor());
        if(!book.getAuthor2().equals("")){
            if(Character.isDigit(book.getAuthor2().charAt(0)))
                row.createCell(10).setCellValue(Integer.parseInt(book.getAuthor2()));
            else row.createCell(10).setCellValue(book.getAuthor2());
        }
        if (Character.isDigit(book.getPublisher().charAt(0)))
            row.createCell(11).setCellValue(Integer.parseInt(book.getPublisher()));
        else row.createCell(11).setCellValue(book.getPublisher());
        row.createCell(12).setCellValue(book.getCategory());
        if(!book.getEnglishTitle().equals(""))
            row.createCell(13).setCellValue(book.getAuthorOriginal());
        row.createCell(15).setCellValue("Opes");
        row.createCell(16).setCellValue("KOR");
        row.createCell(19).setCellValue(book.getPublishDateFortmatted());
        row.createCell(20).setCellValue("Won");
        row.createCell(21).setCellValue(book.getOriginalPriceNumber());
//        row.getCell(21).setCellStyle(yellowBackground);
        row.createCell(22).setCellValue(book.getImageUrl());
        if(!book.getTranslator().equals(""))
            row.createCell(23).setCellValue(book.getTranslator());
        row.createCell(26).setCellValue(book.getBookSizeFormatted());
        row.createCell(27).setCellValue(book.getType());
        row.createCell(28).setCellValue(book.getPages());
        row.createCell(31).setCellValue(book.getWeight());
        row.createCell(32).setCellValue("Books");
        row.createCell(34).setCellValue(0);
        row.createCell(35).setCellValue(1);
        row.createCell(37).setCellValue(1);
        row.createCell(38).setCellValue(0);
    }
    public void saveFile(File saveFile) {
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            logger.log(Level.INFO, "Saving file");
            workbook.write(fos);
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void writeDemo(Book book) {
        writeEntry(startRow, book);
    }
    private void writeEntries(Book[] books) {
        int i = startRow;
        for (Book book : books) {
            writeEntry(i++, book);
        }
    }
    public void writeBooks(Book[] books) {
        retrieveStartRow();
        writeEntries(books);
    }
}
