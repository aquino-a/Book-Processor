package com.aquino.webParser;

import com.aquino.webParser.model.Book;
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

    public void UpdateBook(int rowNum, Book book) {
        var row = sheet.getRow(rowNum);
        if (book.getAuthorId() > 0)
            row.getCell(locationMap.get("author") - 1).setCellValue(book.getAuthorId());
        if (book.getAuthor2Id() > 0)
            row.getCell(locationMap.get("author2") - 1).setCellValue(book.getAuthor2Id());
        if (book.getPublisherId() > 0)
            row.getCell(locationMap.get("publisher") - 1).setCellValue(book.getPublisherId());
    }

    public void setLocationMap(Map<String, Integer> locationMap) {
        this.locationMap = locationMap;
    }
}
