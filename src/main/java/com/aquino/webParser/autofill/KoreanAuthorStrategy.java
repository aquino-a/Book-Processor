package com.aquino.webParser.autofill;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;
import com.aquino.webParser.romanization.Romanizer;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class KoreanAuthorStrategy implements AuthorStrategy {

    private final Map<String, String> koreanLastNames;

    public KoreanAuthorStrategy(Map<String, String> koreanLastNames) {
        this.koreanLastNames = koreanLastNames;
    }

    @Override
    public Author createAuthor(Book book) {
        var author = new Author();
        author.setLanguage(getLanguage());
        SetKoreanNames(author, book);
        return author;
    }

    @Override
    public Language getLanguage() {
        return Language.Korean;
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
}
