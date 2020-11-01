package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private int startRow = 1;
    private Map<String, Integer> locationMap;

    public ExcelReader(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
    }

    public List<Pair<Integer, Book>> ReadBooks(){
        var list = new ArrayList<Pair<Integer,Book>>();
        XSSFRow row = sheet.getRow(startRow);
        for (int i = startRow + 1; row != null; i++, row = sheet.getRow(i)) {
            list.add(Pair.of(i, CreateBook(row)));
        }
        return list;
    }

    private Book CreateBook(XSSFRow row) {
        var book = new Book();
        book.setIsbn(Long.parseLong(row.getCell(locationMap.get("isbn")).getStringCellValue()));
        book.setOclc(Long.parseLong(row.getCell(locationMap.get("oclc")).getStringCellValue()));
        book.setAuthor(row.getCell(locationMap.get("author")).getStringCellValue());
        book.setAuthor2(row.getCell(locationMap.get("author2")).getStringCellValue());
        book.setPublisher(row.getCell(locationMap.get("publisher")).getStringCellValue());
        return book;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setLocationMap(Map<String, Integer> locationMap) {
        this.locationMap = locationMap;
    }
}
