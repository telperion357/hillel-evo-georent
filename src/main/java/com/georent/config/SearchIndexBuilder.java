package com.georent.config;

import com.georent.exception.InitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;

@Component
public class SearchIndexBuilder implements ApplicationListener<ApplicationReadyEvent> {

    private final Session session;

    @Autowired
    public SearchIndexBuilder(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.session = sessionFactory.openSession();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        try {
            fullTextSession.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            throw new InitializationException("Search engine exception.", e);
        }
    }
}
