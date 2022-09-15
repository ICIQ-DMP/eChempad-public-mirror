package org.ICIQ.eChempad.configurations.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * This class contains the beans used in the manipulation of the ACL tables.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AclMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {


    @Bean
    public MethodSecurityExpressionHandler
    defaultMethodSecurityExpressionHandler(DataSource dataSource) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();

        expressionHandler.setPermissionEvaluator(new PermissionEvaluatorCustomImpl(this.aclService(dataSource)));
        return expressionHandler;
    }

    /**
     * You will have to set the classIdentityQuery and sidIdentityQuery properties of JdbcMutableAclService to the following values, respectively:
     *
     * select currval(pg_get_serial_sequence('acl_class', 'id'))
     * select currval(pg_get_serial_sequence('acl_sid', 'id'))
     *
     * From https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/appendix-schema.html
     * @param dataSource Autowired default Datasource (postgreSQL)
     * @return A JdbcMutableService which implements the mutability of the ACL objects
     */
    @Bean
    @Autowired
    public JdbcMutableAclService aclService(DataSource dataSource) {
        JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(dataSource, lookupStrategy(dataSource), aclCache());

        jdbcMutableAclService.setAclClassIdSupported(true);

        jdbcMutableAclService.setClassIdentityQuery("select currval(pg_get_serial_sequence('acl_class', 'id'))");
        jdbcMutableAclService.setSidIdentityQuery("select currval(pg_get_serial_sequence('acl_sid', 'id'))");

        // To use UUIDs in the ACL classes, specified in https://github.com/spring-projects/spring-security/issues/7978

        return jdbcMutableAclService;
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public EhCacheBasedAclCache aclCache() {
        return new EhCacheBasedAclCache(
                aclEhCacheFactoryBean().getObject(),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean() {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(Objects.requireNonNull(this.aclCacheManager().getObject()));
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        return new EhCacheManagerFactoryBean();
    }

    @Bean
    public LookupStrategy lookupStrategy(DataSource dataSource) {
        BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
                dataSource,
                aclCache(),
                aclAuthorizationStrategy(),
                new ConsoleAuditLogger()
        );
        // To use UUIDs in the ACL classes, specified in https://github.com/spring-projects/spring-security/issues/7978
        lookupStrategy.setAclClassIdSupported(true);
        return lookupStrategy;
    }
}