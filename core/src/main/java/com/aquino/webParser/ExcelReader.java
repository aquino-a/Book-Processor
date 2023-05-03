package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ExcelReader {

    private static final Logger LOGGER = LogManager.getLogger();

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private int startRow = 1;
    private Map<String, Integer> locationMap;

    public ExcelReader(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
    }

    public List<Pair<Integer, Book>> ReadBooks() {
        var list = new ArrayList<Pair<Integer, Book>>();
        XSSFRow row = sheet.getRow(startRow);
        for (int i = startRow; row != null && row.getPhysicalNumberOfCells() > 2; i++, row = sheet.getRow(i)) {
            var book = CreateBook(row);

            if (book != null && isNotBlank(book)) {
                list.add(Pair.of(i, book));
            }
        }
        return list;
    }

    private boolean isNotBlank(Book book) {
        return StringUtils.isNotBlank(book.getAuthor()) ||
                StringUtils.isNotBlank(book.getAuthor2()) ||
                StringUtils.isNotBlank(book.getPublisher());
    }

    private Book CreateBook(XSSFRow row) {
        var book = new Book();

        try {
            book.setIsbn((long) row.getCell(locationMap.get("isbn")).getNumericCellValue());
        } catch (Exception e) {
            LOGGER.warn(
                    String.format("Problem with Isbn. Skipping Row. Row#: %d, %s", row.getRowNum(), e.getMessage()));
            return null;
        }

        try {
            book.setOclc((long) row.getCell(locationMap.get("oclc")).getNumericCellValue());
        } catch (Exception e) {
            book.setOclc(-1);
        }

        SetProperty(row, new AuthorSetter(book));
        SetProperty(row, new Author2Setter(book));
        SetProperty(row, new PublisherSetter(book));

        return book;
    }

    private void SetProperty(XSSFRow row, PropertySetter setter) {
        var valueCell = locationMap.get(setter.key);
        var idCell = valueCell - 1;

        try {
            var id = GetNum(row, idCell);
            setter.setId.accept(id);
        } catch (Exception e) {
            LOGGER.warn(String.format("Problem with %s id. Isbn: %d, %s", setter.key, setter.isbn, e.getMessage()));
        }

        try {
            var value = getValue(row, valueCell);
            setter.setValue.accept(value);
        } catch (Exception e) {
            LOGGER.warn(String.format("Problem with %s value. Isbn: %d, %s", setter.key, setter.isbn, e.getMessage()));
        }
    }

    private String getValue(XSSFRow row, int cellNum) {
        var cell = row.getCell(cellNum);
        if (cell == null) {
            throw new NullPointerException("Not found");
        }

        var type = cell.getCellTypeEnum();
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                throw new NullPointerException("Wrong cell type");
        }
    }

    private int GetNum(XSSFRow row, int cellNum) {
        var cell = row.getCell(cellNum);
        if (cell == null)
            throw new NullPointerException("Not found");

        var type = cell.getCellTypeEnum();
        switch (type) {
            case STRING:
                return Integer.parseInt(cell.getStringCellValue());
            case NUMERIC:
                return (int) cell.getNumericCellValue();
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

    private static class AuthorSetter extends PropertySetter {
        public AuthorSetter(Book book) {
            super(
                    book,
                    "author",
                    id -> book.setAuthorId(id),
                    v -> book.setAuthor(v));
        }
    }

    private static class Author2Setter extends PropertySetter {
        public Author2Setter(Book book) {
            super(
                    book,
                    "author2",
                    id -> book.setAuthor2Id(id),
                    v -> book.setAuthor2(v));
        }
    }

    private static class PublisherSetter extends PropertySetter {
        public PublisherSetter(Book book) {
            super(
                    book,
                    "publisher",
                    id -> book.setPublisherId(id),
                    v -> book.setPublisher(v));
        }
    }

    private static class PropertySetter {
        public final String isbn;
        public final String key;
        public final Consumer<Integer> setId;
        public final Consumer<String> setValue;

        public PropertySetter(Book book, String key, Consumer<Integer> setId, Consumer<String> setValue) {
            this.isbn = String.valueOf(book.getIsbn());
            this.key = key;
            this.setId = setId;
            this.setValue = setValue;
        }
    }
}
