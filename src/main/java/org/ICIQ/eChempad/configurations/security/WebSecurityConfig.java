package org.ICIQ.eChempad.configurations.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.enable.csrf}")
    private boolean csrfEnabled;

    /**
     * https://stackoverflow.com/questions/2952196/ant-path-style-patterns
     * @param http HTTP security class
     * @throws Exception Any type of exception that occurs during the HTTP configuration
     */
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
}
