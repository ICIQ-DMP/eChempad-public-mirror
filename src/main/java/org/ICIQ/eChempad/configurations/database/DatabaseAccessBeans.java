/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 10/10/2022
 *
 * This class defines the beans used to configure the access to the database taking in count the ACL implementation.
 * The data that the beans use is autowired from the class {@code DatabaseAccessConfiguration}, which reads all data
 * from {@code application.properties} file.
 *
 * This has been done like this because there was a circularity in the dependency of the beans when declaring
 * data + beans in the same class. It also shows how properties are actually used in an explicit way, instead of relying
 * upon Spring Boot magic to configure things off-stage.
 */
@Component
@Configuration
public class DatabaseAccessBeans {

    /**
     * Class that contains all the relevant data for the configuration of the Database.
     */
    @Autowired
    private DatabaseAccessConfiguration dbAccessConfigurationInstance;

    /**
     * Obtains a {@code DataSource} instance, which is an abstraction upon a Database.
     * @return {@code DataSource} object.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(this.dbAccessConfigurationInstance.getDriver());
        dataSource.setUrl(this.dbAccessConfigurationInstance.getUrl());
        dataSource.setUsername(this.dbAccessConfigurationInstance.getUsername());
        dataSource.setPassword(this.dbAccessConfigurationInstance.getPassword());
        return dataSource;
    }

    /**
     * Returns an {@code EntityManagerFactory}, which is an abstraction over entity data and the Database to retrieve
     * and save entities from a {@code DataSource}.
     * @param dataSource Abstraction over the Database, which contains relevant data to use the database.
     * @param jpaProperties {@code Property} type that contains all relevant information for Hibernate.
     * @return An instance of a class that can easily be used to obtain a {@code EntityManager}.
     */
    @Primary
    @Bean
    @Autowired
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, @Qualifier("hibernateProperties") Properties jpaProperties) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("");
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    /**
     * Returns a {@code LocalSessionFactoryBean}, which is basically an instance of a class that produces Database
     * Sessions.
     * @param dataSource Abstraction over the database, which contains relevant data to use the database.
     * @param hibernateProperties {@code Property} type that contains all relevant information for Hibernate.
     * @return An instance of a class that can easily be used to obtain a {@code Session}.
     */
    @Bean
    @Autowired
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource, Properties hibernateProperties) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }


    /**
     * Retrieves the information needed from the dbAccessConfigurationInstance to build a wrapper instance of type
     * {@code Property} for all the relevant data for Hibernate. This instance is primarily used in the ACL
     * configuration and the BLOB service.
     * @return {@code Property} object, which contains all the relevant data for configuring Hibernate.
     */
    @Bean
    public Properties hibernateProperties()
    {
        Properties hibernateProperties = new Properties();

        // Configures the used database dialect. This allows Hibernate to create SQL queries that are optimized for the used database.
        hibernateProperties.put("hibernate.dialect", this.dbAccessConfigurationInstance.getDialect());

        // If the value of this property is true, Hibernate writes all SQL statements to the console.
        hibernateProperties.put("hibernate.show_sql", this.dbAccessConfigurationInstance.isShowSQL());

        // Specifies the action that is invoked to the database when Hibernate SessionFactory is created or closed.
        hibernateProperties.put("hibernate.hbm2ddl.auto", this.dbAccessConfigurationInstance.getPolicy());

        // If the value of this property is true, Hibernate will format the SQL that is written to the console.
        hibernateProperties.put("hibernate.format_sql", this.dbAccessConfigurationInstance.isFormatSQL());

        // Configures the naming strategy that is used when Hibernate creates new database objects and schema elements
        hibernateProperties.put("hibernate.ejb.naming_strategy", this.dbAccessConfigurationInstance.getNamingStrategy());

        // URL to the database
        hibernateProperties.put("hibernate.connection.url", this.dbAccessConfigurationInstance.getUrl());

        // Username to the database
        hibernateProperties.put("hibernate.connection.username", this.dbAccessConfigurationInstance.getUsername());

        // Password to the database
        hibernateProperties.put("hibernate.connection.password", this.dbAccessConfigurationInstance.getPassword());

        // Current session scope configuration
        hibernateProperties.put("hibernate.current_session_context_class", this.dbAccessConfigurationInstance.getSessionContext());

        return hibernateProperties;
    }

    /**
     * Returns a {@code JpaTransactionManager}, which is the class that on the background produces all the query calls
     * involving JPA entities.
     * @param entityManagerFactory The transaction manager needs an {@code EntityManagerFactory} to operate.
     * @return Instance of {@code JpaTransactionManager}.
     */
    @Bean
    @Autowired
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

}
