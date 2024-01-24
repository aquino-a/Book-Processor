package com.aquino.webParser.autofill;

import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        if (name == null) {
            return;
        }

        setEnglish(author, name);
        setNative(author, name);
    }

    private void setEnglish(Author author, String name) {
        try {
            var split = StringUtils.split(AmazonJapanBookCreator.RomanizeJapanese(name));
            var romanized = Arrays.stream(split)
                .map(word -> StringUtils.capitalize(word))
                .collect(Collectors.joining(" "));

            var names = splitName(romanized);

            author.setEnglishLastName(names.getLeft());
            author.setEnglishFirstName(names.getRight());
        } catch (Exception e) {
            //Don't set if fails.
            LOGGER.error(String.format("Failed to romanize: %s", name));
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void setNative(Author author, String name) {
        var names = splitName(name);

        author.setNativeFirstName(names.getLeft());
        author.setNativeLastName(names.getRight());
    }

    private Pair<String, String> splitName(String name) {
        if (name == null) {
            return null;
        }

        var firstSpace = name.indexOf(' ');
        if (firstSpace < 0) {
            firstSpace = name.indexOf('・');
        }

        if (firstSpace < 0) {
            return Pair.of(name, null);
        }

        var first = name.substring(0, firstSpace);
        var last = name.substring(firstSpace + 1);

        return Pair.of(first, last);
    }
}
