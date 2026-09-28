package com.aquino.webParser.review;

import com.aquino.webParser.model.ReviewPosition;
import org.hibernate.SessionFactory;

import java.util.Optional;

class HibernateReviewPositionRepository {
    private final SessionFactory sessionFactory;

    HibernateReviewPositionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        ensureTable();
    }

    Optional<String> loadLastIsbn(String fileName) {
        var session = sessionFactory.openSession();
        try {
            var row = session.get(ReviewPosition.class, fileName);
            if (row == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(row.getIsbn());
        } finally {
            session.close();
        }
    }

    void saveLastIsbn(String fileName, String isbn) {
        var session = sessionFactory.openSession();
        var tx = session.beginTransaction();
        try {
            var row = session.get(ReviewPosition.class, fileName);
            if (row == null) {
                row = new ReviewPosition();
                row.setFileName(fileName);
            }
            row.setIsbn(isbn);
            session.merge(row);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private void ensureTable() {
        var session = sessionFactory.openSession();
        var tx = session.beginTransaction();
        try {
            session.createNativeQuery(
                            "CREATE TABLE IF NOT EXISTS REVIEW_POSITION (" +
                                    "FILE_NAME VARCHAR(255) PRIMARY KEY, " +
                                    "ISBN VARCHAR(32))")
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}

