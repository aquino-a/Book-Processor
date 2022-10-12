package com.aquino.webParser.autofill;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;

public interface AuthorStrategy {
    Author createAuthor(String name);
    Language getLanguage();
}
