/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security.ACL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * This class contains the beans used in the manipulation of the ACL tables.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 04/10/2022
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AclMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    /**
     * Returns an instance that knows how to evaluate Spring security expressions. This instance delegates the
     * evaluation of permissions to {@code PermissionEvaluatorCustomImpl} which comes preconfigured by Spring out of the
     * box.
     * @param dataSource {@code DataSource} instance to access the database, in order to create the {@code AclService}
     *                                     that we need to create our {@code PermissionEvaluatorCustomImpl}.
     * @return Spring security expression evaluator custom for our ACL needs.
     */
    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler(DataSource dataSource) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();

        expressionHandler.setPermissionEvaluator(new PermissionEvaluatorCustomImpl(this.aclService(dataSource)));
        return expressionHandler;
    }

    /**
     * Tuned ACL service to use UUID as object identity by invoking the {@code setClassIdentityQuery},
     * {@code setAclClassIdSupported} and {@code setSidIdentityQuery} methods of the {@code AclService}. This is used in
     * order to use UUIDs in the ACL classes.
     *
     * @see <a href="https://github.com/spring-projects/spring-security/issues/7978">...</a>
     * @param dataSource Autowired default Datasource (postgreSQL)
     * @return A JdbcMutableService which implements the mutability of the ACL objects
     */
    @Bean
    @Autowired
    public JdbcMutableAclService aclService(DataSource dataSource) {
        JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(dataSource, this.lookupStrategy(dataSource), this.aclCache());

        jdbcMutableAclService.setAclClassIdSupported(true);

        jdbcMutableAclService.setClassIdentityQuery("select currval(pg_get_serial_sequence('acl_class', 'id'))");
        jdbcMutableAclService.setSidIdentityQuery("select currval(pg_get_serial_sequence('acl_sid', 'id'))");

        return jdbcMutableAclService;
    }

    /**
     * Authorization strategy to authorize changes in the ACL tables.
     *
     * @return Object that performs authorizations.
     */
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * To provide a {@code PermissionGrantingStrategy}, which is a component needed by the ACL infrastructure.
     *
     * @return Object that performs permission granting.
     */
    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    /**
     * Cache implementation, which is needed by the ACL infrastructure in order to retrieve ACL entries in an efficient
     * way. In our case we provide an implementation with {@code EhCacheBasedAclCache} which is deprecated.
     *
     * @return An object to manipulate a cache of ACLs.
     */
    @Bean
    public AclCache aclCache() {

        return new EhCacheBasedAclCache(
                aclEhCacheFactoryBean().getObject(),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    /**
     * Bean to provide a factory to create {@code EhCacheBasedAclCache}.
     *
     * @return Object that provides a method to retrieve an {@code AclCache}.
     */
    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean() {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(Objects.requireNonNull(this.aclCacheManager().getObject()));
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    /**
     * Bean to provide an {@code EhCacheManagerFactoryBean}, which is basically a factory class to create
     * {@code AclCache}.
     *
     * @return Object to ease the creation of {@code AclCache}.
     */
    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        return new EhCacheManagerFactoryBean();
    }

    /**
     * Bean to provide an optimized way to retrieve ACLs, using the ACL cache.
     * This class also has a little tuning for using UUID as identity of objects. This occurs when calling the
     * {@code setAclClassIdSupported} in the {@code LookupStrategy} object that we return.
     *
     * @param dataSource Object abstracting the database.
     * @return Object that provides an optimized way to retrieve ACL entries.
     * @see <a href="https://github.com/spring-projects/spring-security/issues/7978">...</a>
     */
    @Bean
    public LookupStrategy lookupStrategy(DataSource dataSource) {
        BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
                dataSource,
                this.aclCache(),
                this.aclAuthorizationStrategy(),
                new ConsoleAuditLogger()
        );

        lookupStrategy.setAclClassIdSupported(true);
        return lookupStrategy;
    }
}