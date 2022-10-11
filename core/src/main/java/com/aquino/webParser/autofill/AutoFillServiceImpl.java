package com.aquino.webParser.autofill;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.ExcelReader;
import com.aquino.webParser.ExcelUpdater;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoFillServiceImpl implements AutoFillService {

    private static final Predicate<String> ID_REGEX =
        Pattern.compile("^[0-9]+( [\u0100-\uFFFF\\w ]+)?$").asPredicate();
    private final BookCreator worldCatBookCreator;
    private final BookWindowService bookWindowService;
    private final Map<String, Integer> locationMap;
    private final Map<Language, AuthorStrategy> authorStrategies;
    private AuthorStrategy currentAuthorStrategy;

    public AutoFillServiceImpl(
        BookCreator worldCatBookCreator,
        BookWindowService bookWindowService,
        Map<String, Integer> locationMap, Map<Language, AuthorStrategy> authorStrategies) {
        this.worldCatBookCreator = worldCatBookCreator;
        this.bookWindowService = bookWindowService;
        this.locationMap = locationMap;
        this.authorStrategies = authorStrategies;
    }

    @Override
    public List<BookWindowIds> readBooks(XSSFWorkbook workbook) {
        var reader = new ExcelReader(workbook);
        reader.setLocationMap(locationMap);
        return reader.ReadBooks()
            .stream()
            .map(this::createBooksWindowsId)
            .filter(bwIds -> bwIds != null)
            .filter(BookWindowIds::isMissingIds)
            .collect(Collectors.toList());
    }

    @Override
    public void updateBook(XSSFWorkbook workbook, List<BookWindowIds> books) {
        var updater = new ExcelUpdater(workbook);
        updater.setLocationMap(locationMap);
        books.forEach(b -> {
            updater.UpdateBook(b.excelRow(), b.book());
        });
    }


    private boolean containsId(String text) {
        return ID_REGEX.test(text);
    }

    private BookWindowIds createBooksWindowsId(Pair<Integer, Book> p) {
        try {
            var book = p.getRight();
            BookWindowIds bookWindowIds;
            if (book.getOclc() > 0) {
                bookWindowIds = getFromWorldCat(book);
            } else {
                bookWindowIds = getWithoutNoOclc(book);
            }

            bookWindowIds.excelRow(p.getLeft());
            bookWindowIds.book(p.getRight());

            return bookWindowIds;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BookWindowIds getFromWorldCat(Book book) throws IOException {
        var afm = new BookWindowIds();
        var wcBook = worldCatBookCreator.createBookFromIsbn(String.valueOf(book.getOclc()));
        afm.author(CreateAuthor(book, wcBook));
        afm.publisher(CreatePublisher(book, wcBook));
        return afm;
    }

    private BookWindowIds getWithoutNoOclc(Book book) {
        var afm = new BookWindowIds();
        afm.author(CreateAuthor(book));
        afm.publisher(CreatePublisher(book));
        return afm;
    }

    private Publisher CreatePublisher(Book book) {
        var author = new Publisher();
        author.setLanguage(currentAuthorStrategy.getLanguage());
        author.setNativeName(book.getPublisher());
        return author;
    }

    @Override
    public Author CreateAuthor(Book book) {
        return currentAuthorStrategy.createAuthor(book);
    }

    private Publisher CreatePublisher(Book book, Book wcBook) {
        if (containsId(book.getPublisher()))
            return null;
        var publisher = new Publisher();
        publisher.setLanguage(currentAuthorStrategy.getLanguage());
        publisher.setEnglishName(wcBook.getPublisher());
        publisher.setNativeName(book.getPublisher());
        return publisher;
    }

    private Author CreateAuthor(Book book, Book wcBook) {
        if (containsId(book.getAuthor())) {
            return null;
        } else if (book.getAuthor().isBlank()) {
            return CreateAuthor(book);
        }
        var author = new Author();
        author.setLanguage(currentAuthorStrategy.getLanguage());
        var split = book.getAuthor().split(" ");
        author.setNativeFirstName(split[0]);
        if (split.length > 1)
            author.setNativeLastName(split[1]);

        split = wcBook.getAuthor().split(" ");
        author.setEnglishLastName(split[0]);
        if (split.length > 1) {
            author.setEnglishFirstName(split[1]);
        }
        return author;
    }

    @Override
    public int insertAuthor(Author author) {
        return bookWindowService.addAuthor(author);
    }

    @Override
    public int insertPublisher(Publisher publisher) {
        return bookWindowService.addPublisher(publisher);
    }

    @Override
    public String getAuthorLink(int id) {
        return bookWindowService.getAuthorLink(String.valueOf(id));
    }

    @Override
    public String getPublisherLink(int id) {
        return bookWindowService.getPublisherLink(String.valueOf(id));
    }

    @Override
    public void setLanguage(Language language) {
        this.currentAuthorStrategy = this.authorStrategies.get(language);
    }
}
