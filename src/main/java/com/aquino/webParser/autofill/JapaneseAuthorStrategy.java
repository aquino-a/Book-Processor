package com.aquino.webParser.autofill;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;

public class JapaneseAuthorStrategy implements AuthorStrategy {

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
        author.setNativeFirstName(book.getAuthor());
    }

}
