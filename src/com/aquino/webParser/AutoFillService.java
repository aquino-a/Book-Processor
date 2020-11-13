package com.aquino.webParser;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.worldcat.WorldCatBookCreator;
import com.aquino.webParser.model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoFillService {

    private static final Predicate<String> ID_REGEX = Pattern.compile("^[0-9]+ [\u0100-\uFFFF\\w ]+$").asPredicate();


    private BookCreator worldCatBookCreator;
    private BookWindowService bookWindowService;

    public AutoFillService(BookCreator worldCatBookCreator, BookWindowService bookWindowService) {
        this.worldCatBookCreator = worldCatBookCreator;
        this.bookWindowService = bookWindowService;
    }

    public List<AutoFillModel> readBooks(XSSFWorkbook workbook){
        var reader = new ExcelReader(workbook);
        return reader.ReadBooks()
                .stream()
                .filter(p -> containsId(p.getRight().getAuthor()) || containsId(p.getRight().getPublisher()))
                .map(p -> createAutoFillModel(p))
                .filter(afm -> afm != null)
                .collect(Collectors.toList());
    }

    public void updateBook(XSSFWorkbook workbook, List<Pair<Integer, Book>> books){
        var updater = new ExcelUpdater(workbook);
        books.forEach(b ->{
            updater.UpdateBook(b.getLeft(), b.getRight());
        });
    }


    private boolean containsId(String text) {
        return ID_REGEX.test(text);
    }

    private AutoFillModel createAutoFillModel(Pair<Integer, Book> p) {
        try {
            var book = p.getRight();
            var wcBook = worldCatBookCreator.createBookFromIsbn(String.valueOf(p.getRight().getOclc()));
            var afm = new AutoFillModel();
            afm.setAuthor(CreateAuthor(book, wcBook));
            afm.setPublisher(CreatePublisher(book, wcBook));
            afm.setBookPair(p);
            return afm;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Publisher CreatePublisher(Book book, Book wcBook) {
        if(containsId(book.getPublisher()))
            return null;
        var publisher = new Publisher();
        publisher.setLanguage(Language.Japanese);
        publisher.setEnglishName(wcBook.getPublisher());
        publisher.setNativeName(book.getPublisher());
        return publisher;
    }

    private Author CreateAuthor(Book book, Book wcBook) {
        if(containsId(book.getAuthor()))
            return null;
        var author = new Author();
        author.setLanguage(Language.Japanese);
        author.setNativeFirstName(book.getAuthor().substring(0,2));
        author.setNativeLastName(book.getAuthor().substring(2,4));

        var split = wcBook.getAuthor().split(",");
        author.setEnglishFirstName(split[0]);
        author.setEnglishFirstName(split[1]);
        return author;
    }

    public int insertAuthor(Author author){
        return bookWindowService.addAuthor(author);
    }

    public int insertPublisher(Publisher publisher){
        return bookWindowService.addPublisher(publisher);
    }


    public String getAuthorLink(int id) {
        return bookWindowService.getAuthorLink(String.valueOf(id));
    }
}
