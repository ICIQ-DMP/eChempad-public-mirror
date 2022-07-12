/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

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
                    .httpBasic(Customizer.withDefaults());

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

        //authenticationBuilder.inMemoryAuthentication().withUser(this.admin_username).password("{noop}" + this.admin_password).roles("USER");

        // https://www.baeldung.com/spring-security-jdbc-authentication
        // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
        // https://stackoverflow.com/questions/51642604/jdbcauthentication-instead-of-inmemoryauthentication-doesnt-give-access-s
        // Get all userdetails and load them into the authentication manager

        //LoggerFactory.getLogger(EChempadApplication.class).info("THIS IS THE BEGIN");

        //UserDetails user = User.builder().username("eChempad").password(passwordEncoder().encode("chemistry")).authorities("USER", "ADMIN").build();

        authenticationBuilder.userDetailsService(this.userDetailsService()).passwordEncoder(this.passwordEncoder()).and()
                .jdbcAuthentication().dataSource(this.dataSource)
                .usersByUsernameQuery("select email, hashed_password as passw, true from researcher where email = ?")
                .authoritiesByUsernameQuery("SELECT researcher.email, CONCAT(elementpermission.journal_id, '_', elementpermission.authority)\n" +
                        "FROM researcher, elementpermission\n" +
                        "WHERE elementpermission.researcher = researcher.uuid \n" +
                        "AND researcher.email = ?");
                        //.withUser(user);  // Add admin account
                //.withUser("patatero").password(passwordEncoder().encode("password")).roles("USER");

        /**
        for (UserDetails userDetails: this.researcherService.loadAllUserDetails().values())
        {
            am.withUser(userDetails);
            Thread.sleep(10000);
            LoggerFactory.getLogger(EChempadApplication.class).info("FOOL FOOL FOOOL FOOL " + userDetails.toString());
        }
         **/

        //am.getUserDetailsService().createUser(this.researcherService.loadDetailsByUsername("admin@eChempad.com"));
        //am.withUser(User.withUsername(this.admin_username).password(passwordEncoder().encode(this.admin_password)).roles("USER", "ADMIN"));


        //LoggerFactory.getLogger(EChempadApplication.class).info("THIS IS THE END");

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
