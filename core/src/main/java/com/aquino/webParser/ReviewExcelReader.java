package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewExcelReader implements ExcelReader {

    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, Integer> locationMap;

    private final XSSFSheet sheet;
    private final DataFormatter dataFormatter = new DataFormatter();
    private int startRow = 1;

    public ReviewExcelReader(XSSFWorkbook workbook) {
        this.sheet = workbook.getSheetAt(0);
    }

    public void setLocationMap(Map<String, Integer> locationMap) {
        this.locationMap = locationMap;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    @Override
    public List<Pair<Integer, Book>> readBooks() {
        var books = new ArrayList<Pair<Integer, Book>>();
        for (int rowIndex = startRow; ; rowIndex++) {
            var row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }
            var book = readRow(row);
            if (book != null) {
                books.add(Pair.of(rowIndex, book));
            }
        }
        return books;
    }

    private Book readRow(XSSFRow row) {
        var book = new Book();

        var isbn = readIsbn(row);
        if (isbn == null) {
            return null;
        }
        book.setIsbn(isbn.numeric);
        book.setIsbnString(isbn.raw);

        book.setBookPageUrl(readString(row, col("bookPageUrl", 2)));
        book.setOclc(readLong(row, col("oclc", 3), -1));

        book.setEnglishTitle(readString(row, col("englishTitle", 4)));
        book.setRomanizedTitle(readString(row, col("romanizedTitle", 6)));
        book.setTitle(readString(row, col("title", 7)));
        book.setTranslatedTitle(readString(row, col("translatedTitle", 8)));

        book.setAuthorId(readInt(row, col("authorId", 9), -1));
        book.setAuthor(readString(row, col("author", 10)));
        book.setAuthorBooks(readString(row, col("authorBooks", 11)));

        book.setAuthor2Id(readInt(row, col("author2Id", 12), -1));
        book.setAuthor2(readString(row, col("author2", 13)));
        book.setAuthor2Books(readString(row, col("author2Books", 14)));

        book.setPublisherId(readInt(row, col("publisherId", 15), -1));
        book.setPublisher(readString(row, col("publisher", 16)));
        book.setPublisherBooks(readString(row, col("publisherBooks", 17)));

        book.setCategory(readString(row, col("category", 18)));
        book.setCategory2(readString(row, col("category2", 19)));
        book.setCategory3(readString(row, col("category3", 20)));
        book.setVendorName(readString(row, col("vendorName", 21)));
        book.setLanguageCode(readString(row, col("languageCode", 22)));
        book.setAuthorOriginal(readString(row, col("authorOriginal", 24)));
        book.setPublishDateFormatted(readString(row, col("publishDateFormatted", 25)));
        book.setCurrencyType(readString(row, col("currencyType", 26)));
        book.setOriginalPriceNumber(readInt(row, col("originalPriceNumber", 27), 0));

        book.setImageURL(readString(row, col("imageURL", 28)));
        book.setTranslator(readString(row, col("translator", 29)));
        book.setAgeGroup(readString(row, col("ageGroup", 31)));
        book.setBookSizeFormatted(readString(row, col("bookSizeFormatted", 32)));
        book.setCover(readString(row, col("type", 33)));
        book.setPages(readInt(row, col("pages", 34), 0));
        book.setWeight(readInt(row, col("weight", 37), 0));

        if (isEffectivelyBlank(book)) {
            return null;
        }
        return book;
    }

    private boolean isEffectivelyBlank(Book book) {
        return book.getIsbn() <= 0
                && StringUtils.isBlank(book.getIsbnString())
                && StringUtils.isBlank(book.getTitle())
                && StringUtils.isBlank(book.getEnglishTitle())
                && StringUtils.isBlank(book.getAuthor())
                && StringUtils.isBlank(book.getPublisher());
    }

    private static final class IsbnValue {
        private final long numeric;
        private final String raw;

        private IsbnValue(long numeric, String raw) {
            this.numeric = numeric;
            this.raw = raw;
        }
    }

    private IsbnValue readIsbn(XSSFRow row) {
        try {
            var cell = row.getCell(col("isbn", 0));
            var value = readIsbnCell(cell);
            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            LOGGER.debug("Failed reading ISBN at row {}: {}", row.getRowNum(), e.getMessage());
        }

        try {
            var cell = row.getCell(col("isbn2", 1));
            var value = readIsbnCell(cell);
            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            LOGGER.debug("Failed reading ISBN at row {}: {}", row.getRowNum(), e.getMessage());
        }

        return null;
    }

    private int col(String key, int fallback) {
        if (locationMap == null) {
            return fallback;
        }
        return locationMap.getOrDefault(key, fallback);
    }

    private IsbnValue readIsbnCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            var isbn = (long) cell.getNumericCellValue();
            if (isbn <= 0) {
                return null;
            }
            return new IsbnValue(isbn, Long.toString(isbn));
        }

        var text = dataFormatter.formatCellValue(cell);
        if (text == null) {
            return null;
        }
        var normalized = text.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        var digits = normalized.replaceAll("[^0-9]", "");
        if (!digits.isEmpty()) {
            try {
                var isbn = Long.parseLong(digits);
                if (isbn > 0) {
                    return new IsbnValue(isbn, normalized);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return new IsbnValue(-1, normalized);
    }

    private String readString(XSSFRow row, int columnIndex) {
        try {
            var cell = row.getCell(columnIndex);
            return cell == null ? "" : dataFormatter.formatCellValue(cell);
        } catch (Exception e) {
            return "";
        }
    }

    private int readInt(XSSFRow row, int columnIndex, int defaultValue) {
        try {
            var cell = row.getCell(columnIndex);
            if (cell == null) {
                return defaultValue;
            }
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            var text = dataFormatter.formatCellValue(cell);
            if (text == null || text.isBlank()) {
                return defaultValue;
            }
            return (int) Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private long readLong(XSSFRow row, int columnIndex, long defaultValue) {
        try {
            var cell = row.getCell(columnIndex);
            if (cell == null) {
                return defaultValue;
            }
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            }
            var text = dataFormatter.formatCellValue(cell);
            if (text == null || text.isBlank()) {
                return defaultValue;
            }
            return (long) Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
