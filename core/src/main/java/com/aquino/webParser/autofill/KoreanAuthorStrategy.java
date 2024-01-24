package com.aquino.webParser.autofill;

import com.aquino.webParser.model.Author;
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
    public Author createAuthor(String name) {
        var author = new Author();
        author.setLanguage(getLanguage());
        SetKoreanNames(author, name);
        return author;
    }

    @Override
    public Language getLanguage() {
        return Language.Korean;
    }

    /**
     * Sets the korean name of the {@link Author} from the {@param name}.
     * Bookswindows puts the korean last name as the first name.
     * The author's native first and last name are set as the full korean name.
     *
     * @param author the author to set the names of.
     * @param name   the book to use to get the names.
     */
    private void SetKoreanNames(Author author, String name) {
        author.setNativeFirstName(name);
        author.setNativeLastName(name);

        if (StringUtils.isBlank(name))
            return;

        var first = name.substring(0, 1);
        first = koreanLastNames.containsKey(first)
            ? koreanLastNames.get(first)
            : Romanizer.hangulToRoman(first);

        author.setEnglishLastName(first);


        if (name.length() > 1) {

            var last = Arrays.stream(name.substring(1)
                .split(""))
                .map(s -> StringUtils.capitalize(Romanizer.hangulToRoman(s)))
                .collect(Collectors.joining(" "));
            author.setEnglishFirstName(last);
        }
    }
}
