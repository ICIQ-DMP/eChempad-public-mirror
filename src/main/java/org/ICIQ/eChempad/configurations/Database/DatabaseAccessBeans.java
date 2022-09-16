package org.ICIQ.eChempad.configurations.Database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * This class defines the beans used to configure the access to the database taking in count the ACL implementation.
 * The data that the beans are used is autowired from the class DBAccessConfiguration, which reads all data from
 * application.properties file. This has been done like this because there was a circularity in the dependency of the
 * beans when declaring data + beans in the same class.
 */
@Component
@Configuration
public class DatabaseAccessBeans {

    @Autowired
    private DatabaseAccessConfiguration dbAccessConfigurationInstance;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        Logger.getGlobal().info(this.dbAccessConfigurationInstance.toString());

        dataSource.setDriverClassName(this.dbAccessConfigurationInstance.getDriver());
        dataSource.setUrl(this.dbAccessConfigurationInstance.getUrl());
        dataSource.setUsername(this.dbAccessConfigurationInstance.getUsername());
        dataSource.setPassword(this.dbAccessConfigurationInstance.getPassword());
        return dataSource;
    }

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

    @Bean
    @Autowired
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource, Properties hibernateProperties) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }


    @Bean
    public Properties hibernateProperties()
    {
        Properties hibernateProperties = new Properties();

        //Configures the used database dialect. This allows Hibernate to create SQL
        //that is optimized for the used database.
        hibernateProperties.put("hibernate.dialect", this.dbAccessConfigurationInstance.getDialect());

        //If the value of this property is true, Hibernate writes all SQL
        //statements to the console.
        hibernateProperties.put("hibernate.show_sql", this.dbAccessConfigurationInstance.isShowSQL());

        //Specifies the action that is invoked to the database when the Hibernate
        //SessionFactory is created or closed.
        hibernateProperties.put("hibernate.hbm2ddl.auto", this.dbAccessConfigurationInstance.getPolicy());

        //If the value of this property is true, Hibernate will format the SQL
        //that is written to the console.
        hibernateProperties.put("hibernate.format_sql", this.dbAccessConfigurationInstance.isFormatSQL());

        //Configures the naming strategy that is used when Hibernate creates
        //new database objects and schema elements
        hibernateProperties.put("hibernate.ejb.naming_strategy", this.dbAccessConfigurationInstance.getNamingStrategy());

        return hibernateProperties;
    }

    @Bean
    @Autowired
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

}
