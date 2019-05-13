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
    private void writeEntry(int rowNumber, OldBook oldBook) {
        if(oldBook.getDoc() == null) return;
        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCellStyle isbnNumberFormat = workbook.createCellStyle();
        isbnNumberFormat.setDataFormat(workbook.createDataFormat().getFormat("#####"));
        XSSFCellStyle yellowBackground = workbook.createCellStyle();
        yellowBackground.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        if(oldBook.getISBN() == -1) {
            row.createCell(0).setCellValue(oldBook.getIsbnString());
            row.createCell(1).setCellValue(oldBook.getIsbnString());
        } else {
            row.createCell(0).setCellValue(oldBook.getISBN());
            row.getCell(0).setCellStyle(isbnNumberFormat);
            row.createCell(1).setCellValue(oldBook.getISBN());
            row.getCell(1).setCellStyle(isbnNumberFormat);
        }
        if(oldBook.getOCLC() != -1)
            row.createCell(3).setCellValue(oldBook.getOCLC());
        if(!oldBook.getEnglishTitle().equals(""))
            row.createCell(4).setCellValue(oldBook.getEnglishTitle());
        row.createCell(6).setCellValue(Romanizer.hangulToRoman(oldBook.getTitle()));
        row.createCell(7).setCellValue(oldBook.getTitle());
        if(Character.isDigit(oldBook.getAuthor().charAt(0)))
            row.createCell(9).setCellValue(Integer.parseInt(oldBook.getAuthor()));
        else row.createCell(9).setCellValue(oldBook.getAuthor());
        if(!oldBook.getAuthor2().equals("")){
            if(Character.isDigit(oldBook.getAuthor2().charAt(0)))
                row.createCell(10).setCellValue(Integer.parseInt(oldBook.getAuthor2()));
            else row.createCell(10).setCellValue(oldBook.getAuthor2());
        }
        if (Character.isDigit(oldBook.getPublisher().charAt(0)))
            row.createCell(11).setCellValue(Integer.parseInt(oldBook.getPublisher()));
        else row.createCell(11).setCellValue(oldBook.getPublisher());
        row.createCell(12).setCellValue(oldBook.getCategory());
        if(!oldBook.getEnglishTitle().equals(""))
            row.createCell(13).setCellValue(oldBook.getAuthorOriginal());
        row.createCell(15).setCellValue("Opes");
        row.createCell(16).setCellValue("KOR");
        row.createCell(19).setCellValue(oldBook.getPublishDateFortmatted());
        row.createCell(20).setCellValue("Won");
        row.createCell(21).setCellValue(oldBook.getOriginalPriceNumber());
//        row.getCell(21).setCellStyle(yellowBackground);
        row.createCell(22).setCellValue(oldBook.getImageUrl());
        if(!oldBook.getTranslator().equals(""))
            row.createCell(23).setCellValue(oldBook.getTranslator());
        row.createCell(26).setCellValue(oldBook.getBookSizeFormatted());
        row.createCell(27).setCellValue(oldBook.getType());
        row.createCell(28).setCellValue(oldBook.getPages());
        row.createCell(31).setCellValue(oldBook.getWeight());
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
    public void writeDemo(OldBook oldBook) {
        writeEntry(startRow, oldBook);
    }
    private void writeEntries(OldBook[] oldBooks) {
        int i = startRow;
        for (OldBook oldBook : oldBooks) {
            writeEntry(i++, oldBook);
        }
    }
    public void writeBooks(OldBook[] oldBooks) {
        retrieveStartRow();
        writeEntries(oldBooks);
    }
}
