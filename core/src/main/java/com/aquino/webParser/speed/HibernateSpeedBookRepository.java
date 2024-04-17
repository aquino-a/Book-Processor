package com.aquino.webParser.speed;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.aquino.webParser.bookCreators.aladin.web.AladinCategory;

public class HibernateSpeedBookRepository implements SpeedBookRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    private SessionFactory sessionFactory;
    private Session currentSession;

    public HibernateSpeedBookRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public CompletableFuture<Stream<SpeedBook>> getByCategory(AladinCategory category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByCategory'");
    }

    @Override
    public void save(SpeedBook book) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public void update(SpeedBook book) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
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
