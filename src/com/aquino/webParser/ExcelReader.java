package com.aquino.webParser;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcelReader {

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private int startRow = 1;
    private Map<String, Integer> locationMap = Stream.of(new Object[][] {
        { "isbn", 0 },
        { "oclc", 3 },
        { "author", 9 },
        { "author2", 10 },
        { "publisher", 11 }
    }).collect(Collectors.toMap(data -> (String) data[0],  data -> (int) data[1]));

    public ExcelReader(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
    }

    public List<Pair<Integer,Book>> ReadBooks(){
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
}
