package com.aquino.webParser.autofill;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.ExcelReader;
import com.aquino.webParser.ExcelUpdater;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.*;
import org.apache.commons.lang3.StringUtils;
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
                .filter(p -> hasMissingIds(p.getRight()))
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
            BookWindowIds bookWindowIds = null;
            if (book.getOclc() > 0) {
                bookWindowIds = getFromWorldCat(book);
            }

            if (bookWindowIds == null) {
                bookWindowIds = getWithoutNoOclc(book);
            }

            bookWindowIds.author().setId(book.getAuthorId());
            bookWindowIds.author2().setId(book.getAuthor2Id());
            bookWindowIds.publisher().setId(book.getPublisherId());

            bookWindowIds.excelRow(p.getLeft());
            bookWindowIds.book(p.getRight());

            return bookWindowIds;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BookWindowIds getFromWorldCat(Book book) throws IOException {
        var ids = new BookWindowIds();
        var wcBook = worldCatBookCreator.createBookFromIsbn(String.valueOf(book.getOclc()));
        if (wcBook == null) {
            return null;
        }

        ids.author(CreateAuthor(book.getAuthor(), wcBook.getAuthor()));
        ids.author2(CreateAuthor(book.getAuthor2(), wcBook.getAuthor2()));
        ids.publisher(CreatePublisher(book, wcBook));

        return ids;
    }

    /**
     * Determines if the book has any missing ids.
     * If no ids are missing then it can be ignored.
     */
    private boolean hasMissingIds(Book book) {
        var noAuthorId = !StringUtils.isBlank(book.getAuthor()) && book.getAuthorId() < 1;
        var noAuthorId2 = !book.getAuthor2().equals("1494") &&
                !StringUtils.isBlank(book.getAuthor2()) &&
                book.getAuthor2Id() < 1;
        var noPublisherId = !StringUtils.isBlank(book.getPublisher()) && book.getPublisherId() < 1;

        return noAuthorId || noAuthorId2 || noPublisherId;
    }

    private BookWindowIds getWithoutNoOclc(Book book) {
        var ids = new BookWindowIds();
        ids.author(CreateAuthor(book.getAuthor()));
        ids.author2(CreateAuthor(book.getAuthor2()));
        ids.publisher(CreatePublisher(book));

        return ids;
    }

    private Publisher CreatePublisher(Book book) {
        var author = new Publisher();
        author.setLanguage(currentAuthorStrategy.getLanguage());
        author.setNativeName(book.getPublisher());
        return author;
    }

    @Override
    public Author CreateAuthor(String name) {
        return currentAuthorStrategy.createAuthor(name);
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

    private Author CreateAuthor(String name, String wcName) {
        if (containsId(name)) {
            return null;
        } else if (StringUtils.isBlank(name) || StringUtils.isBlank(wcName)) {
            return CreateAuthor(name);
        }
        var author = new Author();
        author.setLanguage(currentAuthorStrategy.getLanguage());
        var split = name.split(" ");
        author.setNativeFirstName(split[0]);
        if (split.length > 1)
            author.setNativeLastName(split[1]);

        split = wcName.split(" ");
        author.setEnglishFirstName(split[0]);
        if (split.length > 1) {
            author.setEnglishLastName(split[1]);
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
