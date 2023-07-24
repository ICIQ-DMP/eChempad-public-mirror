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
package org.ICIQ.eChempad.configurations.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * "Special" singleton class to store the sessionFactory and retrieve it comfortably, in order to work rawly with
 * sessions. It is special because in a singleton class the constructor(s) must be private to ensure that only one
 * instance exists. In this case on the contrary, the constructor is public because it needs to be visible by Spring.
 * Anyway, since we do not use the constructor there is no danger in setting it as public.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 10/10/2022
 */
@org.springframework.context.annotation.Configuration
public class HibernateUtil {

    /**
     * The actual {@code SessionFactory} that we are storing.
     */
    private static SessionFactory sessionFactory;

    /**
     * The instance of this class, which is what is makes a singleton.
     */
    private static HibernateUtil hibernateUtil;

    /**
     * Constructor of this class, which is a special constructor since it initializes a static variable of the same
     * type of this class. This ensures that only one {@code SessionFactory} is used in the application.
     *
     * @param hibernateProperties Relevant properties to configure Hibernate.
     */
    @Autowired
    public HibernateUtil(Properties hibernateProperties)
    {
        try {
            // Create the SessionFactory from hibernate properties
            Configuration configuration = new Configuration().setProperties(hibernateProperties);
            Properties properties = configuration.getProperties();
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(properties);
            sessionFactory = configuration.buildSessionFactory(builder.build());
            HibernateUtil.hibernateUtil = this;  // singleton
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Obtains an instance of the {@code HibernateUtil} class. Differently from a typical singleton pattern, we do not
     * initialize our instance if this method is called and the field is null. instead, we show an error.
     *
     * @return Unique instance of the {@code HibernateUtil} class.
     */
    public static HibernateUtil getInstance() {
        if (HibernateUtil.hibernateUtil == null)
        {
            //TODO throw exception
            Logger.getGlobal().info("HIBERNATE UTIL INSTANCE HAS NOT BEEN INITIALIZED BY SPRING");
        }
        return HibernateUtil.hibernateUtil;
    }

    /**
     * Obtains directly the underlying {@code SessionFactory} of the {@code HibernateUtil} instance. If it is null shows
     * an error.
     *
     * @return {@code SessionFactory} instance, to easily obtain a {@code Session} to the database.
     */
    public static SessionFactory getSessionFactory()
    {
        if (HibernateUtil.hibernateUtil == null)
        {
            // TODO throw exception
            Logger.getGlobal().info("HIBERNATE UTIL INSTANCE HAS NOT BEEN INITIALIZED BY SPRING");
        }
        return HibernateUtil.sessionFactory;
    }

    /**
     * Close caches and connection pools.
     */
    public void shutdown() {
        HibernateUtil.getSessionFactory().close();
    }
}
