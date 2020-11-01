package com.aquino.webParser;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map;

public class ExcelUpdater {

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;

    private Map<String, Integer> locationMap;

    public ExcelUpdater(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
    }

    public void UpdateBook(int rowNum, Book book){
        var row = sheet.getRow(rowNum);
        row.getCell(locationMap.get("author")).setCellValue(book.getAuthor());
        row.getCell(locationMap.get("author2")).setCellValue(book.getAuthor2());
        row.getCell(locationMap.get("publisher")).setCellValue(book.getPublisher());
    }

    public void setLocationMap(Map<String, Integer> locationMap) {
        this.locationMap = locationMap;
    }
}
