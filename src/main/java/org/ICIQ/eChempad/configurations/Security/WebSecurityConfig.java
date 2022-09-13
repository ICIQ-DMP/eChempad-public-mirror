/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.Security;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${eChempad.security.csrf}")
    private boolean csrfDisabled;

    @Value("${eChempad.security.csrf}")
    private boolean corsDisabled;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * https://stackoverflow.com/questions/2952196/ant-path-style-patterns
     * Allow everyone to access the login and logout form and allow everyone to access the login API calls.
     * Allow only authenticated users to access the API.
     * @param http HTTP security class. Can be used to configure a lot of different parameters regarding HTTP security.
     * @throws Exception Any type of exception that occurs during the HTTP configuration
     */
    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {
        // Conditional activation depending on the profile properties
        // https://www.yawintutor.com/how-to-enable-and-disable-csrf/
        if (! this.csrfDisabled) {
            http.csrf().disable();
        }

        if (! this.corsDisabled) {
            http.cors().disable();
        }

        http
                // Creates the http form login in the default URL /login· The first parameter is a string corresponding
                // to the URL where we will map the login form
                    .formLogin()
                    .loginPage("/login")
                .permitAll()

                .and()
                .authorizeRequests()

                    .antMatchers("/api/researcher").authenticated()
                    .antMatchers("/api/journal").authenticated()
                    .antMatchers("/api/experiment").authenticated()
                    .antMatchers("/api/document").authenticated()
                    .antMatchers("/api/**").authenticated()

                // https://stackoverflow.com/questions/57574981/what-is-httpbasic-method-in-spring-security
                // allows the basic HTTP authentication. If the user cannot be authenticated using HTTP auth headers it
                // will show a 401 unauthenticated*/
                .and()
                    .httpBasic();

    }


    /**
     * https://www.arteco-consulting.com/securizando-una-aplicacion-con-spring-boot/
     * WARNING! This method is executed before the initialization method in ApplicationStartup, so if the database is
     * not already initialized with the users that we want to authorize we will not be able to input requests from any
     * users, even if they have been loaded afterwards.
     * @param authenticationBuilder Object instance used to build authentication objects.
     * @throws Exception Any type of exception
     */
    @Autowired
    @Transactional
    public void configureGlobal(AuthenticationManagerBuilder authenticationBuilder) throws Exception
    {
        authenticationBuilder
                    // Provide the service to retrieve user details
                    .userDetailsService(this.userDetailsService)
                    // Provide the password encoder used to store password in the database
                    .passwordEncoder(WebSecurityConfig.passwordEncoder())
                .and();
                //    .jdbcAuthentication()

                        // Provide the datasource to retrieve authentication data from
                  //      .dataSource(this.dataSource);
                        // Provide a query to obtain all the triplets (username, password and account_status)
                        //.usersByUsernameQuery("SELECT username, password, true FROM researcher WHERE username = ?")
                        // Provide a query to obtain all the tuples (username, security_principal_name)
                        //.authoritiesByUsernameQuery("SELECT researcher.username,acl_sid.sid FROM researcher, acl_sid WHERE researcher.id = acl_sid.id AND researcher.username = ? AND acl_sid.principal = true");
    }


    /**
     * Bean that returns the password encoder used for hashing passwords
     * @return Returns an instance of encode, which can be used by accessing to encode(String) method
     */
    @Bean()
    public static PasswordEncoder passwordEncoder()
    {
        return NoOpPasswordEncoder.getInstance();
        //return new BCryptPasswordEncoder();
    }

    @Bean
    public static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
