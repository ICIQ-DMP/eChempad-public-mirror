/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class LobServiceImpl implements LobService {

    @Override
    public Blob createBlob(InputStream content, long size) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Blob blob = session.getLobHelper().createBlob(content, size);

        session.getTransaction().commit();
        session.close();
        return blob;
    }

    /**
     * Retrieves the InputStream associated with a Blob.
     *
     * @param blob Binary large object reference in the database
     * @return The InputStream from the BLOB
     */
    @Override
    public InputStream readBlob(Blob blob) {

        // Delimit transaction boundaries explicitly to access the BLOB
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        // Read InputStream from BLOB
        InputStream is = null;
        try {
            is = blob.getBinaryStream();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Delimit transaction boundaries. End operation.
        session.getTransaction().commit();
        session.close();

        return is;
    }
}
