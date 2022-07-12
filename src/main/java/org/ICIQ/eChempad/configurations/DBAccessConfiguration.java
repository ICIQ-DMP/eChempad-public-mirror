package org.ICIQ.eChempad.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.logging.Logger;

@RestController
//@Configuration
public class DBAccessConfiguration {
    @Value("${eChempad.db.driver}")
    private String driver;
    private static String DRIVER;

    @Value("${eChempad.db.dialect}")
    private String dialect;
    private static String DIALECT;

    @Value("${eChempad.db.url}")
    private String url;
    private static String URL;

    @Value("${eChempad.db.username}")
    private String username;
    private static String USERNAME;

    @Value("${eChempad.db.password}")
    private String password;
    private static String PASSWORD;

    @Value("${eChempad.db.policy}")
    private String policy;
    private static String POLICY;

    @Value("${eChempad.db.show-sql}")
    private boolean showSQL;
    private static boolean SHOWSQL;

    @Value("${eChempad.db.format_sql}")
    private boolean formatSQL;
    private static boolean FORMATSQL;

    @Value("${eChempad.db.naming_strategy}")
    private String namingStrategy;
    private static String NAMINGSTRATEGY;


    @Bean
    public static DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DBAccessConfiguration.DRIVER);
        dataSource.setUrl(DBAccessConfiguration.URL);
        dataSource.setUsername(DBAccessConfiguration.USERNAME);
        dataSource.setPassword(DBAccessConfiguration.PASSWORD);
        return dataSource;
    }

    @Bean
    @Autowired
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Properties jpaProperties) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("");

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Bean
    @Autowired
    public static LocalSessionFactoryBean sessionFactory(DataSource dataSource, Properties hibernateProperties) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }


    @Bean
    public static Properties hibernateProperties()
    {
        Logger.getGlobal().severe("ssssssssssdsddsdsd"+ DBAccessConfiguration.DIALECT);

        Properties hibernateProperties = new Properties();

        Logger.getGlobal().severe("ssssssssssdsddsdsd"+ DBAccessConfiguration.DIALECT);


        //Configures the used database dialect. This allows Hibernate to create SQL
        //that is optimized for the used database.
        hibernateProperties.put("hibernate.dialect", DBAccessConfiguration.DIALECT);

        //If the value of this property is true, Hibernate writes all SQL
        //statements to the console.
        hibernateProperties.put("hibernate.show_sql", DBAccessConfiguration.SHOWSQL);

        //Specifies the action that is invoked to the database when the Hibernate
        //SessionFactory is created or closed.
        hibernateProperties.put("hibernate.hbm2ddl.auto", DBAccessConfiguration.POLICY);

        //If the value of this property is true, Hibernate will format the SQL
        //that is written to the console.
        hibernateProperties.put("hibernate.format_sql", DBAccessConfiguration.FORMATSQL);

        //Configures the naming strategy that is used when Hibernate creates
        //new database objects and schema elements
        hibernateProperties.put("hibernate.ejb.naming_strategy", DBAccessConfiguration.NAMINGSTRATEGY);

        return hibernateProperties;
    }

    @Bean
    @Autowired
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }




    // Setters for moving data from instance fields to static fields, @Value annotation does not work in static fields
    @Value("${eChempad.db.driver}")
    public void setDriver(String driver) {
        DBAccessConfiguration.DRIVER = driver;
    }

    @Value("${eChempad.db.dialect}")
    public void setDialect(String dialect) {
        DBAccessConfiguration.DIALECT = dialect;
    }

    @Value("${eChempad.db.url}")
    public void setUrl(String url) {
        DBAccessConfiguration.URL = url;
    }

    @Value("${eChempad.db.username}")
    public void setUsername(String username) {
        DBAccessConfiguration.USERNAME = username;
    }

    @Value("${eChempad.db.password}")
    public void setPassword(String password) {
        DBAccessConfiguration.PASSWORD = password;
    }

    @Value("${eChempad.db.policy}")
    public void setPolicy(String policy) {
        DBAccessConfiguration.POLICY = policy;
    }

    @Value("${eChempad.db.show-sql}")
    public void setShowSQL(boolean showSQL) {
        DBAccessConfiguration.SHOWSQL = showSQL;
    }

    @Value("${eChempad.db.format_sql}")
    public void setFormatSQL(boolean formatSQL) {
        DBAccessConfiguration.FORMATSQL = formatSQL;
    }

    @Value("${eChempad.db.naming_strategy}")
    public void setNamingStrategy(String namingStrategy) {
        DBAccessConfiguration.NAMINGSTRATEGY = namingStrategy;
    }
}
