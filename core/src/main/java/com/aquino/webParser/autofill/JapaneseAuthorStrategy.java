package com.aquino.webParser.autofill;

import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JapaneseAuthorStrategy implements AuthorStrategy {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Author createAuthor(String name) {
        var author = new Author();
        author.setLanguage(getLanguage());
        SetJapNames(author, name);
        return author;
    }

    @Override
    public Language getLanguage() {
        return Language.Japanese;
    }

    private void SetJapNames(Author author, String name) {
        try {
            var split = StringUtils.split(AmazonJapanBookCreator.RomanizeJapanese(name));
            var romanized = Arrays.stream(split)
                .map(word -> StringUtils.capitalize(word))
                .collect(Collectors.joining(" "));
            author.setEnglishLastName(romanized);
        }
        catch (IOException e) {
            //Don't set if fails.
            LOGGER.error(String.format("Failed to romanize: %s", name));
            LOGGER.error(e.getMessage(), e);
        }

        author.setNativeFirstName(name);
    }

}
