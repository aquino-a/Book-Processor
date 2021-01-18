package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelReader {

    private static final Logger logger = Logger.getLogger(ExcelReader.class.getName());


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
        for (int i = startRow; row != null && row.getPhysicalNumberOfCells() > 2; i++, row = sheet.getRow(i)) {
            list.add(Pair.of(i, CreateBook(row)));
        }
        return list;
    }

    private Book CreateBook(XSSFRow row) {
        var book = new Book();
        book.setIsbn((long) row.getCell(locationMap.get("isbn")).getNumericCellValue());

        try {
            book.setOclc((long) row.getCell(locationMap.get("oclc")).getNumericCellValue());
        } catch (NullPointerException e) {
            book.setOclc(-1);
        }

        SetAuthor(row, book);
        SetAuthor2(row, book);
        SetPublisher(row, book);

        return book;
    }

    private void SetAuthor(XSSFRow row, Book book) {
        var authorCell = locationMap.get("author");
        var authorIdCell = authorCell - 1;

        try {
            var id = GetNum(row, authorIdCell);
            book.setAuthorId(id);
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Problem with author id. Isbn: %d, %s", book.getIsbn(), e.getMessage()));
        }

        var cell = row.getCell(authorCell);
        if(cell != null) {
            book.setAuthor(cell.getStringCellValue());
        }
        else logger.log(Level.WARNING, String.format("Problem with author. Isbn: %d", book.getIsbn()));
    }

    private void SetAuthor2(XSSFRow row, Book book) {
        var author2Cell = locationMap.get("author2");
        var author2IdCell = author2Cell - 1;

        try {
            var id = GetNum(row, author2IdCell);
            book.setAuthor2Id(id);
        } catch (Exception e) {
             logger.log(Level.WARNING, String.format("Problem with author2 id. Isbn: %d, %s", book.getIsbn(), e.getMessage()));
        }

        var cell = row.getCell(author2Cell);
        if(cell != null)
            book.setAuthor2(cell.getStringCellValue());
        else logger.log(Level.WARNING, String.format("Problem with author2. Isbn: %d", book.getIsbn()));

    }

    private void SetPublisher(XSSFRow row, Book book) {
        var publisherCell = locationMap.get("publisher");
        var publisherIdCell = publisherCell - 1;

        try {
            var id = GetNum(row, publisherIdCell);
            book.setPublisherId(id);
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Problem with author2 id. Isbn: %d, %s", book.getIsbn(), e.getMessage()));
        }
        var cell = row.getCell(publisherCell);
        if(cell != null)
            book.setPublisher(cell.getStringCellValue());
        else logger.log(Level.WARNING, String.format("Problem with publisher. Isbn: %d", book.getIsbn()));
    }

    private int GetNum(XSSFRow row, int cellNum){
        var cell = row.getCell(cellNum);
        if(cell == null)
            throw new NullPointerException("Not found");

        var type = cell.getCellType();
        switch (type)  {
            case STRING:
                return Integer.parseInt(cell.getStringCellValue());
            case NUMERIC:
                return (int)cell.getNumericCellValue();
            default:
                throw new NullPointerException("Wrong cell type");
        }
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setLocationMap(Map<String, Integer> locationMap) {
        this.locationMap = locationMap;
    }
}
