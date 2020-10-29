package com.aquino.webParser;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class ExcelReader {

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private int startRow = 1;

    public ExcelReader(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
    }

    public List<Book> ReadBooks(){
        throw new NotImplementedException("");
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }
}
