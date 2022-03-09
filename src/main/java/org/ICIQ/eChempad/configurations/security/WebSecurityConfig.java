package org.ICIQ.eChempad.configurations.security;

import org.ICIQ.eChempad.EChempadApplication;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.services.ResearcherService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // Value obtained from .properties files, outside the env of the JAVA project. Configures Cross-site request forgery
    // protection.
    @Value("${security.enable.csrf}")
    private boolean csrfEnabled;

    @Value("${spring.security.user.name}")
    private String admin_username;

    @Value("${spring.security.user.password}")
    private String admin_password;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * https://stackoverflow.com/questions/2952196/ant-path-style-patterns
     * Allow everyone to access the login and logout form and allow everyone to access the login API calls.
     * Allow only authenticated users to access the API.
     * @param http HTTP security class. Can be used to configure a lot of different parameters regarding HTTP security.
     * @throws Exception Any type of exception that occurs during the HTTP configuration
     */
    //TODO:

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {
        // Conditional activation depending on the profile properties
        // https://www.yawintutor.com/how-to-enable-and-disable-csrf/
        if (this.csrfEnabled) {
            http.csrf().disable();
        }

        http
                .authorizeRequests()

                // Publicly accessible URLs
                .antMatchers("/login/**").permitAll()

                // You have to be authenticated and authorized to have the roll USER to access /api/journal
                .antMatchers("/api/**").hasAnyAuthority("USER", "ADMIN")

                //.hasARole("USER")

                // The rest of requests have to be always authenticated
                .anyRequest().authenticated()

                // Creates the http form login in the default URL /login· The first parameter is a string corresponding to the
                // URL where we will map the login form
                .and().formLogin()

                // https://stackoverflow.com/questions/57574981/what-is-httpbasic-method-in-spring-security
                // allows the basic HTTP authentication. If the user cannot be authenticated using HTTP auth headers it
                // will show a 401 unauthenticated
                .and().httpBasic(Customizer.withDefaults());


    }


    /**
     * https://www.arteco-consulting.com/securizando-una-aplicacion-con-spring-boot/
     * @param authenticationBuilder
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationBuilder) throws Exception
    {

        //authenticationBuilder.inMemoryAuthentication().withUser(this.admin_username).password("{noop}" + this.admin_password).roles("USER");

        // https://www.baeldung.com/spring-security-jdbc-authentication
        // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
        // https://stackoverflow.com/questions/51642604/jdbcauthentication-instead-of-inmemoryauthentication-doesnt-give-access-s
        // Get all userdetails and load them into the authentication manager

        LoggerFactory.getLogger(EChempadApplication.class).info("THIS IS THE BEGIN");

        UserDetails user = User.builder().username("eChempad").password(passwordEncoder().encode("chemistry")).authorities("USER", "ADMIN").build();

        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();

        authenticationBuilder.userDetailsService(this.userDetailsService()).passwordEncoder(this.passwordEncoder()).and()
                .jdbcAuthentication().dataSource(this.dataSource)
                // .withDefaultSchema()  // Does not work with psql
                .usersByUsernameQuery("select email, hashed_password as passw, true from researcher where email = ?")
                .authoritiesByUsernameQuery("SELECT researcher.email, elementpermission.authority\n" +
                        "FROM researcher, elementpermission\n" +
                        "WHERE elementpermission.researcher = researcher.uuid \n" +
                        "AND researcher.email = ?");
                        //.withUser(user);  // Add admin account
                //.withUser("patatero").password(passwordEncoder().encode("pass")).authorities("ROLE_USER");

        session.getTransaction().commit();
        session.close();

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


        LoggerFactory.getLogger(EChempadApplication.class).info("THIS IS THE END");

    }


    /**
     * Bean that returns the password encoder used for hashing passwords
     * @return Returns an instance of encode, which can be used by accessing to encode(String) method
     */
    @Bean()
    public PasswordEncoder passwordEncoder()
    {

        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }


}
