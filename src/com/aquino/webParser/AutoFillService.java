package com.aquino.webParser;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.worldcat.WorldCatBookCreator;
import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.AutoFillModel;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Publisher;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoFillService {

    private static final Predicate<String> ID_REGEX = Pattern.compile("^[0-9]+ [\u0100-\uFFFF\\w ]+$").asPredicate();

    private ExcelUpdater excelUpdater;

    private BookCreator worldCatBookCreator;
    private BookWindowService bookWindowService;

    public List<AutoFillModel> readBooks(XSSFWorkbook workbook){
        var reader = new ExcelReader(workbook);
        return reader.ReadBooks()
                .stream()
                .filter(p -> containsId(p.getRight().getAuthor()) || containsId(p.getRight().getPublisher()))
                .map(p -> createAutoFillModel(p))
                .filter(afm -> afm != null)
                .collect(Collectors.toList());
    }

    private boolean containsId(String text) {
        return ID_REGEX.test(text);
    }

    private AutoFillModel createAutoFillModel(Pair<Integer, Book> p) {
        try {
            var wcBook = worldCatBookCreator.bookListFromIsbn(String.valueOf(p.getRight().getOclc()));
            var afm = new AutoFillModel();
            var author = new Author();
            return new AutoFillModel();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int insertAuthor(Author author){
        return bookWindowService.addAuthor(author);
    }

    public int insertPublisher(Publisher publisher){
        return bookWindowService.addPublisher(publisher);
    }


}
