package com.aquino.webParser;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUpdater {

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;

    public ExcelUpdater(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
    }
}
