package org.ICIQ.eChempad.services;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    @Override
    public Blob createBlob(InputStream content, long size) {
        return this.sessionFactory.getCurrentSession().getLobHelper().createBlob(content, size);
    }

    @Transactional
    @Override
    public InputStream readBlob(Blob blob) {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        InputStream is = null;
        try {
            is = blob.getBinaryStream();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert connection != null;
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return is;
    }
}
