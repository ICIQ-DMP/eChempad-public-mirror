package org.ICIQ.eChempad.configurations.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AclMethodSecurityConfiguration
        extends GlobalMethodSecurityConfiguration {

    final
    DataSource dataSource;

    final
    MethodSecurityExpressionHandler
            defaultMethodSecurityExpressionHandler;

    @Autowired
    public AclMethodSecurityConfiguration(DataSource dataSource, MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler) {
        this.dataSource = dataSource;
        this.defaultMethodSecurityExpressionHandler = defaultMethodSecurityExpressionHandler;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return defaultMethodSecurityExpressionHandler;
    }


    /**
     * Let's also enable Expression-Based Access Control by setting prePostEnabled to true to use Spring Expression
     * Language (SpEL). Moreover, we need an expression handler with ACL support:
     * @return Expression handler
     */
    @Bean
    public MethodSecurityExpressionHandler
    defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler
                = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator
                = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

    /**
     * Hence, we assign AclPermissionEvaluator to the DefaultMethodSecurityExpressionHandler. The evaluator needs a
     * MutableAclService to load permission settings and domain object's definitions from the database.
     * For simplicity, we use the provided JdbcMutableAclService.
     * As its name, the JdbcMutableAclService uses JDBCTemplate to simplify database access. It needs a DataSource
     * (for JDBCTemplate), LookupStrategy (provides an optimized lookup when querying the database), and an AclCache
     * (caching ACL Entries and Object Identity).
     * @return ACL services
     */
    @Bean
    public JdbcMutableAclService aclService() {
        return new JdbcMutableAclService(
                dataSource, lookupStrategy(), aclCache());
    }


    /**
     * Here, the AclAuthorizationStrategy is in charge of concluding whether a current user possesses all required
     * permissions on certain objects or not.
     * @return Authorization strategy.
     */
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(
                new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(
                new ConsoleAuditLogger());
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
        ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }


    /**
     * Used to manage the cache of the ACL, so it can be cached and queries and lookups are faster.
     * @return The cache manager
     */
    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        return new EhCacheManagerFactoryBean();
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(
                dataSource,
                aclCache(),
                aclAuthorizationStrategy(),
                new ConsoleAuditLogger()
        );
    }

}