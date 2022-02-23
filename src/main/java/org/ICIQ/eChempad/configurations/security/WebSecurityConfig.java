package org.ICIQ.eChempad.configurations.security;

import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.services.ResearcherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ResearcherService researcherService;

    // Value obtained from .properties files, outside the env of the JAVA project
    @Value("${security.enable.csrf}")
    private boolean csrfEnabled;

    /**
     * https://stackoverflow.com/questions/2952196/ant-path-style-patterns
     * Allow everyone to access the login and logout form and allow everyone to access the login API calls.
     * Allow only authenticated users to access the API.
     * @param http HTTP security class
     * @throws Exception Any type of exception that occurs during the HTTP configuration
     */
    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {

        http
                .authorizeRequests()  // Protect all API REST directions
                    .antMatchers("/api/document", "/api/experiment", "/api/journal", "/api/researcher").authenticated()

                .and().authorizeRequests()  // Allow requests to the API REST calls for login
                    .antMatchers("/api/login")
                    .permitAll()

                .and().formLogin()  // Allow requests to the login form
                    .loginPage("/login")
                    .permitAll()

                .and().logout()  // Allow logout (?)
                    .permitAll();

        // Conditional activation depending on the profile properties
        // https://www.yawintutor.com/how-to-enable-and-disable-csrf/
        if (this.csrfEnabled)
        {
            http.csrf().disable();
        }

    }


    /**
     * https://www.arteco-consulting.com/securizando-una-aplicacion-con-spring-boot/
     * @param authenticationBuilder
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationBuilder) throws Exception
    {
        authenticationBuilder.userDetailsService(researcherService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
