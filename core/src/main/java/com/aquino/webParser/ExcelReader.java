package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ExcelReader {

    private static final Logger LOGGER = LogManager.getLogger();

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private int startRow = 1;
    private Map<String, Integer> locationMap;

    private PropertySetter authorSetter;
    private PropertySetter author2Setter;
    private PropertySetter publisherSetter;

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
            var cell = row.getCell(locationMap.get("isbn"));
            if (cell == null || !cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                return null;
            }

            var isbn = (long) cell.getNumericCellValue();
            book.setIsbn(isbn);
        } catch (Exception e) {
            LOGGER.warn("Problem with Isbn. Skipping Row. Row#: {}, {}", row.getRowNum(), e.getMessage());
            return null;
        }

        try {
            book.setOclc((long) row.getCell(locationMap.get("oclc")).getNumericCellValue());
        } catch (Exception e) {
            book.setOclc(-1);
        }

        authorSetter.SetProperty(row, book);
        author2Setter.SetProperty(row, book);
        publisherSetter.SetProperty(row, book);

        return book;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setLocationMap(Map<String, Integer> locationMap) {
        this.locationMap = locationMap;
        authorSetter = new AuthorSetter(locationMap);
        author2Setter = new Author2Setter(locationMap);
        publisherSetter = new PublisherSetter(locationMap);
    }

    private static class AuthorSetter extends PropertySetter {
        public AuthorSetter(Map<String, Integer> locationMap) {
            super(
                    locationMap,
                    "author",
                    (b, id) -> b.setAuthorId(id),
                    (b, v) -> b.setAuthor(v),
                    (b, v) -> b.setAuthorBooks(v));
        }
    }

    private static class Author2Setter extends PropertySetter {
        public Author2Setter(Map<String, Integer> locationMap) {
            super(
                    locationMap,
                    "author2",
                    (b, id) -> b.setAuthor2Id(id),
                    (b, v) -> b.setAuthor2(v),
                    (b, v) -> b.setAuthor2Books(v));
        }
    }

    private static class PublisherSetter extends PropertySetter {
        public PublisherSetter(Map<String, Integer> locationMap) {
            super(
                    locationMap,
                    "publisher",
                    (b, id) -> b.setPublisherId(id),
                    (b, v) -> b.setPublisher(v),
                    (b, v) -> b.setPublisherBooks(v));
        }
    }

    private static class PropertySetter {
        private final String key;
        private final BiConsumer<Book, Integer> setId;
        private final BiConsumer<Book, String> setValue;
        private final BiConsumer<Book, String> setBW;
        private final Map<String, Integer> locationMap;

        private final DataFormatter dataFormatter = new DataFormatter();

        public PropertySetter(
                Map<String, Integer> locationMap,
                String key,
                BiConsumer<Book, Integer> setId,
                BiConsumer<Book, String> setValue,
                BiConsumer<Book, String> setBW) {
            this.locationMap = locationMap;
            this.key = key;
            this.setId = setId;
            this.setValue = setValue;
            this.setBW = setBW;
        }

        public void SetProperty(XSSFRow row, Book book) {
            var valueCell = locationMap.get(key);
            var idCell = valueCell - 1;
            var bwCell = valueCell + 1;

            try {
                var id = getNum(row, idCell);
                setId.accept(book, id);

            } catch (CellNotFoundException ce) {
                LOGGER.error("Cell not found. Cell: {}, Key: {}, Row num: {}, ISBN: {}", idCell, key, row.getRowNum(), book.getIsbn());
            } catch (Exception e) {
                LOGGER.error("Id Cell. Cell: {}, Key: {}, Row num: {}, ISBN: {}, Message: {}", idCell, key, row.getRowNum(), book.getIsbn(), e.getMessage());
            }

            try {
                var cell = row.getCell(valueCell);
                var value = dataFormatter.formatCellValue(cell);

                setValue.accept(book, value);
            } catch (Exception e) {
                LOGGER.error("Value Cell. Cell: {}, Key: {}, Row num: {}, ISBN: {}, Message: {}", valueCell, key, row.getRowNum(), book.getIsbn(), e.getMessage());
            }

            try {
                var cell = row.getCell(bwCell);
                var value = dataFormatter.formatCellValue(cell);

                setBW.accept(book, value);
            } catch (Exception e) {
                LOGGER.error("BW Cell. Cell: {}, Key: {}, Row num: {}, ISBN: {}, Message: {}", bwCell, key, row.getRowNum(), book.getIsbn(), e.getMessage());
            }
        }

        private int getNum(XSSFRow row, int cellNum) {
            var cell = row.getCell(cellNum);
            if (cell == null)
                throw new CellNotFoundException();

            var type = cell.getCellTypeEnum();
            switch (type) {
                case STRING:
                    return Integer.parseInt(cell.getStringCellValue());
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                default:
                    throw new IllegalArgumentException(
                            String.format("Wrong cell type: %s", type));
            }
        }

        private class CellNotFoundException extends RuntimeException {

        }
    }
}
