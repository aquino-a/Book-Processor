package com.aquino.webParser.autofill;

import com.aquino.webParser.model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface AutoFillService {
    List<AutoFillModel> readBooks(XSSFWorkbook workbook);

    void updateBook(XSSFWorkbook workbook, List<Pair<Integer, Book>> books);

    Author CreateAuthor(Book book);

    int insertAuthor(Author author);

    int insertPublisher(Publisher publisher);

    String getAuthorLink(int id);

    void setLanguage(Language language);
}
