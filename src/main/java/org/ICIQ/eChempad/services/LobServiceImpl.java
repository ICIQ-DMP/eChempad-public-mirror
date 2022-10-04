package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.database.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.env.spi.LobCreatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.postgresql.util.PSQLException;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class LobServiceImpl implements LobService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateUtil hibernateUtil;

    @Transactional
    @Override
    public Blob createBlob(InputStream content, long size) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Blob blob = session.getLobHelper().createBlob(content, size);

        session.getTransaction().commit();
        session.close();
        return blob;
    }

    @Transactional
    @Override
    public InputStream readBlob(Blob blob) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        InputStream is = null;
        try {
            is = blob.getBinaryStream();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        session.getTransaction().commit();
        session.close();

        return is;
    }
}
