/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.Arrays;

/**
 * This class is the most important security orchestrator. It defines what URL endpoints are accessible under what
 * circumstances and configures security mechanisms such as CSRF and CORS.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1/3/2023
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Sets the state of the CSRF protection
     */
    @Value("${eChempad.security.csrf}")
    private boolean csrfDisabled;

    /**
     * Sets the state of the CORS protection
     */
    @Value("${eChempad.security.csrf}")
    private boolean corsDisabled;

    /**
     * To provide the {@code UserDetailsService} for authentication purposes.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Allow everyone to access the login and logout form and allow everyone to access the login API calls.
     * Allow only authenticated users to access the API.
     *
     * @see <a href="https://www.zkoss.org/wiki/ZK%20Developer's%20Reference/Security%20Tips/Cross-site%20Request%20Forgery">...</a>
     * @see <a href="https://stackoverflow.com/questions/2952196/ant-path-style-patterns">...</a>
     * @see <a href="https://stackoverflow.com/questions/57574981/what-is-httpbasic-method-in-spring-security">...</a>
     * @see <a href="https://javabydeveloper.com/refused-to-display-in-a-frame-because-it-set-x-frame-options-to-deny-in-spring/">...</a>
     * @param http HTTP security class. Can be used to configure a lot of different parameters regarding HTTP security.
     * @throws Exception Any type of exception that occurs during the HTTP configuration
     */
    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {

        // ZUL files regexp execution time
        String zulFiles = "/zkau/web/**/*.zul";

        // Web files regexp execution time
        String[] zkResources = {"/zkau/web/**/js/**", "/zkau/web/**/zul/css/**", "/zkau/web/**/img/**"};

        // Allow desktop cleanup after logout or when reloading login page
        String removeDesktopRegex = "/zkau\\?dtid=.*&cmd_0=rmDesktop&.*";

        // you need to disable spring CSRF to make ZK AU pass security filter
        // ZK already sends a AJAX request with a built-in CSRF token,
        http.csrf().disable();

        // Conditional activation depending on profile
        if (! this.corsDisabled) {
            http.cors().disable();
        }

        http
                .headers().frameOptions().sameOrigin() // X-Frame-Options = SAMEORIGIN

                .and()
                    .authorizeRequests()
                    .antMatchers("/api/authority").authenticated()
                    .antMatchers("/api/researcher").authenticated()
                    .antMatchers("/api/journal").authenticated()
                    .antMatchers("/api/experiment").authenticated()
                    .antMatchers("/api/document").authenticated()
                    .antMatchers("/api/**").authenticated()

                // allows the basic HTTP authentication. If the user cannot be authenticated using HTTP auth headers it
                // will show a 401 unauthenticated*/
                .and()
                    .httpBasic()

                // For the GUI with ZKoss
                .and()
                .authorizeRequests()
                    .antMatchers(zulFiles).denyAll()  // Block direct access to zul files
                    .antMatchers(HttpMethod.GET, zkResources).permitAll()  // Allow ZK resources
                    .regexMatchers(HttpMethod.GET, removeDesktopRegex).permitAll()  // Allow desktop cleanup
                    // Allow desktop cleanup from ZATS
                    .requestMatchers(req -> "rmDesktop".equals(req.getParameter("cmd_0"))).permitAll()
                    // Allow unauthenticated access to login or logout pages
                    .mvcMatchers("/login","/logout", "/timeout").permitAll()
                    // Only allow authenticated users in the secure endpoints
                    .mvcMatchers("/secure/**").hasRole("USER")
                    // Only allow authenticated users in the API endpoint
                    .mvcMatchers("/api/**").hasRole("USER")
                    // Any other requests has to be authenticated too
                    .anyRequest().authenticated()

                // Creates the http form login in the default URL /login· The first parameter is a string corresponding
                // to the URL where we will map the login form
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/")  // Successful redirect URL after login is root page

                // Creates a logout form
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/");  // After logout, redirect to main page
    }


    /**
     * Configures the authentication. Currently, it is done simply by providing a {@code UserDetailsService}, which
     * uses the database to get user information username and password
     * WARNING! This method is executed before the initialization method in ApplicationStartup, so if the database is
     * not already initialized with the users that we want to authorize we will not be able to input requests from any
     * users, even if they have been loaded afterwards.
     *
     * @see <a href="https://www.arteco-consulting.com/securizando-una-aplicacion-con-spring-boot/">...</a>
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
    }


    /**
     * Bean that returns the password encoder used for hashing passwords.
     *
     * @return Returns an instance of encode, which can be used by accessing to encode(String) method
     */
    @Bean()
    public static PasswordEncoder passwordEncoder()
    {
        return NoOpPasswordEncoder.getInstance();  // TODO: In production change to a safe password encoder
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
