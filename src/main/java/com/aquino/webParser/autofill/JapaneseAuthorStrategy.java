package com.aquino.webParser.autofill;

import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JapaneseAuthorStrategy implements AuthorStrategy {

    private static final Logger LOGGER = Logger.getLogger(JapaneseAuthorStrategy.class.getName());

    @Override
    public Author createAuthor(Book book) {
        var author = new Author();
        author.setLanguage(getLanguage());
        SetJapNames(author, book);
        return author;
    }

    @Override
    public Language getLanguage() {
        return Language.Japanese;
    }

    private void SetJapNames(Author author, Book book) {
        try {
            author.setEnglishLastName(AmazonJapanBookCreator.RomanizeJapanese(book.getAuthor()));
        }
        catch (IOException e) {
            //Don't set if fails.
            LOGGER.log(Level.WARNING, String.format("Failed to romaninze: %s", book.getAuthor()));
        }

        author.setNativeFirstName(book.getAuthor());
    }

}
