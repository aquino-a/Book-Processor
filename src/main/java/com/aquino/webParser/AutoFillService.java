package com.aquino.webParser;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.*;
import com.aquino.webParser.romanization.Romanizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoFillService {

    private static final Predicate<String> ID_REGEX =
        Pattern.compile("^[0-9]+( [\u0100-\uFFFF\\w ]+)?$").asPredicate();
    private final BookCreator worldCatBookCreator;
    private final BookWindowService bookWindowService;
    private final Map<String, Integer> locationMap;
    private Map<String, String> koreanLastNames;
    private Language language;

    public AutoFillService(BookCreator worldCatBookCreator, BookWindowService bookWindowService, Map<String, Integer> locationMap) {
        this.worldCatBookCreator = worldCatBookCreator;
        this.bookWindowService = bookWindowService;
        this.locationMap = locationMap;
        this.language = Language.Japanese;
    }

    public List<AutoFillModel> readBooks(XSSFWorkbook workbook) {
        var reader = new ExcelReader(workbook);
        reader.setLocationMap(locationMap);
        return reader.ReadBooks()
            .stream()
//                .filter(p -> !containsId(p.getRight().getAuthor()) || !containsId(p.getRight().getPublisher()))
//                .filter(p -> p.getRight().getAuthorId() != -1 || p.getRight().getPublisherId() != -1)
            .filter(p -> p.getRight().getAuthorId() == -1 && p.getRight().getAuthor() != null)
            .map(p -> createAutoFillModel(p))
            .filter(afm -> afm != null)
            .collect(Collectors.toList());
    }

    public void updateBook(XSSFWorkbook workbook, List<Pair<Integer, Book>> books) {
        var updater = new ExcelUpdater(workbook);
        updater.setLocationMap(locationMap);
        books.forEach(b -> {
            updater.UpdateBook(b.getLeft(), b.getRight());
        });
    }


    private boolean containsId(String text) {
        return ID_REGEX.test(text);
    }

    private AutoFillModel createAutoFillModel(Pair<Integer, Book> p) {
        try {
            var book = p.getRight();
            AutoFillModel afm;
            if (book.getOclc() > 0) {
                afm = getWorldCatModel(book);
            }
            else {
                afm = getNoOclcModel(book);
            }
            afm.setBookPair(p);
            return afm;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AutoFillModel getWorldCatModel(Book book) throws IOException {
        var afm = new AutoFillModel();
        var wcBook = worldCatBookCreator.createBookFromIsbn(String.valueOf(book.getOclc()));
        afm.setAuthor(CreateAuthor(book, wcBook));
        afm.setPublisher(CreatePublisher(book, wcBook));
        return afm;
    }

    private AutoFillModel getNoOclcModel(Book book) {
        var afm = new AutoFillModel();
        afm.setAuthor(CreateAuthor(book));
        afm.setPublisher(CreatePublisher(book));
        return afm;
    }

    private Publisher CreatePublisher(Book book) {
        var author = new Publisher();
        author.setLanguage(language);
        author.setNativeName(book.getPublisher());
        return author;
    }

    public Author CreateAuthor(Book book) {
        var author = new Author();
        author.setLanguage(language);
        switch (language) {
            case Korean:
                SetKoreanNames(author, book);
            case Japanese:
            default:
                SetJapNames(author, book);
        }
        return author;
    }


    /**
     * Sets the korean name of the {@link Author} from the {@link Book}.
     * Bookswindows puts the korean last name as the first name.
     * The author's native first and last name are set as the full korean name.
     *
     * @param author the author to set the names of.
     * @param book   the book to use to get the names.
     */
    private void SetKoreanNames(Author author, Book book) {
        author.setNativeFirstName(book.getAuthor());
        author.setNativeLastName(book.getAuthor());

        if (book.getAuthor().isBlank())
            return;

        var first = book.getAuthor().substring(0, 1);
        first = koreanLastNames.containsKey(first)
            ? koreanLastNames.get(first)
            : Romanizer.hangulToRoman(first);

        author.setEnglishFirstName(first);


        if (book.getAuthor().length() > 1) {

            var last = Arrays.stream(book.getAuthor().substring(1)
                .split(""))
                .map(s -> StringUtils.capitalize(Romanizer.hangulToRoman(s)))
                .collect(Collectors.joining(" "));
            author.setEnglishLastName(last);
        }
    }

    private void SetJapNames(Author author, Book book) {
        author.setNativeFirstName(book.getAuthor());
    }

    private Publisher CreatePublisher(Book book, Book wcBook) {
        if (containsId(book.getPublisher()))
            return null;
        var publisher = new Publisher();
        publisher.setLanguage(language);
        publisher.setEnglishName(wcBook.getPublisher());
        publisher.setNativeName(book.getPublisher());
        return publisher;
    }

    private Author CreateAuthor(Book book, Book wcBook) {
        if (containsId(book.getAuthor()))
            return null;
        var author = new Author();
        author.setLanguage(language);
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

    public int insertAuthor(Author author) {
        return bookWindowService.addAuthor(author);
    }

    public int insertPublisher(Publisher publisher) {
        return bookWindowService.addPublisher(publisher);
    }


    public String getAuthorLink(int id) {
        return bookWindowService.getAuthorLink(String.valueOf(id));
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setKoreanLastNames(Map<String, String> koreanLastNames) {
        this.koreanLastNames = koreanLastNames;
    }
}
