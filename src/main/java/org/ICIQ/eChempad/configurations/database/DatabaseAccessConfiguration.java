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
 * Contains (and collects) all data coming from application.properties regarding database config.
 */
@Configuration
public class DatabaseAccessConfiguration {
    /**
     * Contains the 
     */
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
