package org.ICIQ.eChempad.configurations.security;

import org.ICIQ.eChempad.EChempadApplication;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.services.ResearcherService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.logging.Logger;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ResearcherService researcherService;

    // Value obtained from .properties files, outside the env of the JAVA project. Configures Cross-site request forgery
    // protection.
    @Value("${security.enable.csrf}")
    private boolean csrfEnabled;

    @Value("${spring.security.user.name}")
    private String admin_username;

    @Value("${spring.security.user.password}")
    private String admin_password;


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
        if (this.csrfEnabled) {
            http.csrf().disable();
        }

        //
        http
                .authorizeRequests()

                // Publicly accessible URLs
                .antMatchers("/login*", "/api/researcher/**").permitAll()

                // You have to be authenticated and authorized to have the roll USER to access /api/journal
                .antMatchers("/api/journal").hasRole("USER")

                // The rest of requests have to be always authenticated
                .anyRequest().authenticated()

                // Creates the http form login in the default URL /login· The first parameter is a string corresponding to the
                // URL where we will map the login form
                .and().formLogin()

                // https://stackoverflow.com/questions/57574981/what-is-httpbasic-method-in-spring-security
                // allows the basic HTTP authentication. If the user cannot be authenticated using HTTP auth headers it
                // will show a 401 unauthenticated
                .and().httpBasic();



        // Creates the http form login in the default URL /login· The first parameter is a string corresponding to the
        // URL where we will map the login form
        http.formLogin();
    }


    /**
     * https://www.arteco-consulting.com/securizando-una-aplicacion-con-spring-boot/
     * @param authenticationBuilder
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationBuilder) throws Exception
    {

        LoggerFactory.getLogger(EChempadApplication.class).info(
                "SDFGSDFGGFGAFGSFASFASFSA" + this.admin_password + " " + this.admin_username);

        authenticationBuilder
                .inMemoryAuthentication()
                .withUser(this.admin_username).password("{noop}" + this.admin_password).roles("USER");
    }
}
