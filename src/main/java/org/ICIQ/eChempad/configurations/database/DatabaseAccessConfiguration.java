/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 10/10/2022
 *
 * Contains (and collects) all data coming from application.properties regarding database config.
 */
@Configuration
public class DatabaseAccessConfiguration {
    /**
     * Contains the {@code String} that identifies a database driver that is used for the database.
     */
    @Value("${eChempad.db.driver}")
    private String driver;

    /**
     * Contains the {@code String} that identifies an SQL dialect that is used for the database.
     */
    @Value("${eChempad.db.dialect}")
    private String dialect;

    /**
     * Contains the URL used to connect to the database.
     */
    @Value("${eChempad.db.url}")
    private String url;

    /**
     * Contains the username used for the database connection.
     */
    @Value("${eChempad.db.username}")
    private String username;

    /**
     * Contains the password used for the database connection.
     */
    @Value("${eChempad.db.password}")
    private String password;

    /**
     * Determines the policy regarding database tables every time the application is started. This configuration is used
     * to choose if we need to upgrade our schema if there are changes, drop it, do nothing...
     * It can be set to create-drop, create, update or none. This option exists as a developer utility, so it should not
     * be used in production.
     */
    @Value("${eChempad.db.policy}")
    private String policy;

    /**
     * Determines if SQL is logged into the console. Should be set to false in production.
     */
    @Value("${eChempad.db.show-sql}")
    private boolean showSQL;

    /**
     * Determines if the SQL is formatted into the console.
     */
    @Value("${eChempad.db.format_sql}")
    private boolean formatSQL;

    /**
     * Contains an {@code String} that determines the strategy to use when naming columns and tables in the database
     * side regarding the names of the corresponding variables in the code. This is because JPA provides an abstraction
     * over entities in the database, and so it needs a way to name the table that is created for a JPA entity.
     */
    @Value("${eChempad.db.naming_strategy}")
    private String namingStrategy;

    /**
     * Determines at what level the Session should be bounded to the code. You can have a {@code Session} for each
     * process, for each thread or for each request.
     */
    @Value("${eChempad.db.sessionContext}")
    private String sessionContext;

    public DatabaseAccessConfiguration() {}

    public String getDriver() {
        return driver;
    }

    @Value("${eChempad.db.driver}")
    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDialect() {
        return dialect;
    }

    @Value("${eChempad.db.dialect}")
    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getUrl() {
        return url;
    }

    @Value("${eChempad.db.url}")
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    @Value("${eChempad.db.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @Value("${eChempad.db.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPolicy() {
        return policy;
    }

    @Value("${eChempad.db.policy}")
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public boolean isShowSQL() {
        return showSQL;
    }

    @Value("${eChempad.db.show-sql}")
    public void setShowSQL(boolean showSQL) {
        this.showSQL = showSQL;
    }

    public boolean isFormatSQL() {
        return formatSQL;
    }

    @Value("${eChempad.db.format_sql}")
    public void setFormatSQL(boolean formatSQL) {
        this.formatSQL = formatSQL;
    }

    public String getNamingStrategy() {
        return namingStrategy;
    }

    @Value("${eChempad.db.naming_strategy}")
    public void setNamingStrategy(String namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public String getSessionContext() {
        return sessionContext;
    }

    @Value("${eChempad.db.sessionContext}")
    public void setSessionContext(String sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public String toString() {
        return "DBAccessConfiguration{" +
                "driver='" + driver + '\'' +
                ", dialect='" + dialect + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", policy='" + policy + '\'' +
                ", showSQL=" + showSQL +
                ", formatSQL=" + formatSQL +
                ", namingStrategy='" + namingStrategy + '\'' +
                '}';
    }
}
