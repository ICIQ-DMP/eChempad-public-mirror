package org.ICIQ.eChempad.configurations.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;
import java.util.logging.Logger;

@org.springframework.context.annotation.Configuration
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    private static HibernateUtil hibernateUtil;

    // Singleton
    @Autowired
    public HibernateUtil(Properties hibernateProperties)
    {

        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Logger.getGlobal().info("the PROPETIESS" + hibernateProperties.toString());

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

    public static HibernateUtil getInstance() {
        if (HibernateUtil.hibernateUtil == null)
        {
            // Init
            Logger.getGlobal().info("HIBERNATE UTIL INSTANCE HAS NOT BEEN INITIALIZED BY SPRING");
        }
        return HibernateUtil.hibernateUtil;
    }

    public static SessionFactory getSessionFactory()
    {
        if (HibernateUtil.hibernateUtil == null)
        {
            // Init
            Logger.getGlobal().info("HIBERNATE UTIL INSTANCE HAS NOT BEEN INITIALIZED BY SPRING");
        }
        return HibernateUtil.sessionFactory;
    }

    public void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}
