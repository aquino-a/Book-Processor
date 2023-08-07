package com.aquino.webParser.chatgpt;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.aquino.webParser.model.SavedBook;

public class HibernateSummaryRepository implements SummaryRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    private SessionFactory sessionFactory;
    private Session currentSession;

    public HibernateSummaryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String get(String isbn) {
        var book = getBook(isbn);
        if (book == null) {
            return null;
        }

        return book.getSummary();
    }

    @Override
    public void save(String isbn, String summary) {
        var book = getBookToSave(isbn);

        book.setSummary(summary);
        
        var session = getSession();
        session.merge(book);
    }

    @Override
    public String getTitle(String isbn) {
        var book = getBook(isbn);
        if (book == null) {
            return null;
        }

        return book.getTranslatedTitle();
    }

    @Override
    public void saveTitle(String isbn, String title) {
        var book = getBookToSave(isbn);

        book.setTranslatedTitle(title);
        
        var session = getSession();
        session.merge(book);
    }

    private SavedBook getBookToSave(String isbn) {
        var book = getBook(isbn);
        if (book == null) {
            book = new SavedBook();
            book.setIsbn(isbn);
        }

        return book;
    }

    @Override
    public String getCategory(String isbn) {
        var book = getBook(isbn);
        if (book == null) {
            return null;
        }

        return book.getCategory();
    }

    @Override
    public void saveCategory(String isbn, String combinedCodes) {
        var book = getBookToSave(isbn);

        book.setCategory(combinedCodes);
        
        var session = getSession();
        session.merge(book);
    }

    @Override
    public String getNativeSummary(String isbn) {
        var book = getBook(isbn);
        if (book == null) {
            return null;
        }

        return book.getNativeSummary();
    }

    @Override
    public void saveNativeSummary(String isbn, String nativeSummary) {
        var book = getBookToSave(isbn);

        book.setNativeSummary(nativeSummary);
        
        var session = getSession();
        session.merge(book);
    }

    private SavedBook getBook(String isbn) {
        var session = getSession();
        var book = session.get(SavedBook.class, isbn);
        if (book == null) {
            LOGGER.log(Level.INFO, "No data found for ISBN = " + isbn);
            return null;
        }

        return book;
    }

    private Session getSession() {
        if (currentSession == null || !currentSession.isConnected() || !currentSession.isOpen()) {
            if (currentSession != null) {
                currentSession.close();
            }

            currentSession = sessionFactory.openSession();
        }

        return currentSession;
    }
}
