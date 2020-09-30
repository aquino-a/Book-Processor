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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        if(book.getAuthor() != null && !book.getAuthor().equals("") && IsStringAllNum(book.getAuthor()))
            row.createCell(9).setCellValue(Integer.parseInt(book.getAuthor()));
        else row.createCell(9).setCellValue(book.getAuthor());
        if(!book.getAuthor2().equals("")){
            if(IsStringAllNum(book.getAuthor2()))
                row.createCell(10).setCellValue(Integer.parseInt(book.getAuthor2()));
            else row.createCell(10).setCellValue(book.getAuthor2());
        }
        if (IsStringAllNum(book.getPublisher()))
            row.createCell(11).setCellValue(Integer.parseInt(book.getPublisher()));
        else row.createCell(11).setCellValue(book.getPublisher());
        row.createCell(12).setCellValue(book.getCategory());
        if(!book.getEnglishTitle().equals(""))
            row.createCell(13).setCellValue(book.getAuthorOriginal());
        row.createCell(15).setCellValue(book.getVendorName());
        row.createCell(16).setCellValue(book.getLanguageCode());
        row.createCell(19).setCellValue(book.getPublishDateFormatted());
        row.createCell(20).setCellValue(book.getCurrencyType());
        row.createCell(21).setCellValue(book.getOriginalPriceNumber());
//        row.getCell(21).setCellStyle(yellowBackground);
        row.createCell(22).setCellValue(book.getImageURL());
        row.getCell(22).setCellStyle(workbook.createCellStyle());
        if(!book.getTranslator().equals(""))
            row.createCell(23).setCellValue(book.getTranslator());
        row.createCell(24).setHyperlink(createHyperLink(book.getImageURL()));
        row.getCell(24).setCellValue("Image");
        row.createCell(26).setCellValue(book.getBookSizeFormatted());
        row.createCell(27).setCellValue(book.getType());
        row.createCell(28).setCellValue(book.getPages());
        row.createCell(31).setCellValue(book.getWeight());
        row.createCell(32).setCellValue("Books");
        row.createCell(34).setCellValue(0);
        row.createCell(35).setCellValue(1);
        row.createCell(37).setCellValue(1);
        row.createCell(38).setCellValue(0);
        book.getMiscellaneous().forEach(ei -> {
            if(ei.getColumnNumber() <= 38){
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
            logger.log(Level.INFO, "Saving file");
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
