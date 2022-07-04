package org.ICIQ.eChempad.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DBAccessConfiguration {

    @Value("${eChempad.db.driver}")
    private String driver;

    @Value("${eChempad.db.dialect}")
    private String dialect;

    @Value("${eChempad.db.url}")
    private String url;

    @Value("${eChempad.db.username}")
    private String username;

    @Value("${eChempad.db.password}")
    private String password;

    @Value("${eChempad.db.policy}")
    private String policy;

    @Value("${eChempad.db.show-sql}")
    private boolean showSQL;

    @Value("${eChempad.db.format_sql}")
    private boolean formatSQL;

    @Value("${eChempad.db.naming_strategy}")
    private String namingStrategy;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(this.driver);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(this.dataSource());

        Properties hibernateProperties = new Properties();

        //Configures the used database dialect. This allows Hibernate to create SQL
        //that is optimized for the used database.
        hibernateProperties.put("hibernate.dialect", this.dialect);

        //If the value of this property is true, Hibernate writes all SQL
        //statements to the console.
        hibernateProperties.put("hibernate.show_sql", this.showSQL);

        //Specifies the action that is invoked to the database when the Hibernate
        //SessionFactory is created or closed.
        hibernateProperties.put("hibernate.hbm2ddl.auto", this.policy);

        //If the value of this property is true, Hibernate will format the SQL
        //that is written to the console.
        hibernateProperties.put("hibernate.format_sql", this.formatSQL);

        //Configures the naming strategy that is used when Hibernate creates
        //new database objects and schema elements
        hibernateProperties.put("hibernate.ejb.naming_strategy", this.namingStrategy);

        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }
}
