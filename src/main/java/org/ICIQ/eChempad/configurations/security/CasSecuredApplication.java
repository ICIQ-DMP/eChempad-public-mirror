package org.ICIQ.eChempad.configurations.security;


import jakarta.servlet.http.HttpSessionEvent;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.session.SingleSignOutHttpSessionListener;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Collections;


@Configuration
public class CasSecuredApplication {

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }



    @Bean
    @Primary
    public AuthenticationManager authenticationManager(CasAuthenticationProvider casAuthenticationProvider) {
        return new ProviderManager(Collections.singletonList(casAuthenticationProvider));
    }

    /*
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(HttpSecurity http, org.springframework.security.core.userdetails.UserDetailsService userDetailService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(this.passwordEncoder())
                .and()
                .build();
    }*/

    @Bean
    @Primary
    public CasAuthenticationFilter casAuthenticationFilter(
            ServiceProperties serviceProperties, AuthenticationManager authenticationManager) throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setServiceProperties(serviceProperties);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService("https://echempad.iciq.es:8081/login/cas");
        serviceProperties.setSendRenew(false);
        //serviceProperties.setArtifactParameter(DEFAULT_CAS_ARTIFACT_PARAMETER);
        return serviceProperties;
    }

    @Bean
    @Primary
    public AuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties)
    {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties);
        casAuthenticationEntryPoint.setLoginUrl("https://echempad-cas.iciq.es:8443/cas/login");
        return casAuthenticationEntryPoint;

    }

    @Bean
    public TicketValidator ticketValidator() {
        return new Cas30ServiceTicketValidator("https://echempad-cas.iciq.es:8443/cas");
    }

    @Bean
    @Primary
    public CasAuthenticationProvider casAuthenticationProvider(
            TicketValidator ticketValidator,
            ServiceProperties serviceProperties,
            UserDetailsServiceImpl userDetailsService) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties);
        provider.setTicketValidator((org.apereo.cas.client.validation.TicketValidator) ticketValidator);

        // Static login
        // TODO parametrize in production
        //provider.setUserDetailsService(s -> new User("casuser", "Mellon", true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_ADMIN")));

        provider.setUserDetailsService(userDetailsService);
        provider.setKey("CAS_PROVIDER_LOCALHOST_8081");
        return provider;
    }

    @Bean
    public AuthenticationUserDetailsService<Authentication> authenticationUserDetailsService(UserDetailsServiceImpl userDetailsService) {
        UserDetailsByNameServiceWrapper<Authentication> serviceWrapper = new UserDetailsByNameServiceWrapper<>();
        serviceWrapper.setUserDetailsService(userDetailsService);
        return serviceWrapper;
    }

    // logout

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public LogoutFilter logoutFilter(SecurityContextLogoutHandler securityContextLogoutHandler) {
        LogoutFilter logoutFilter = new LogoutFilter(
                "https://echempad-cas.iciq.es:8443/cas/logout",
                securityContextLogoutHandler);
        logoutFilter.setFilterProcessesUrl("/logout/cas");
        return logoutFilter;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setLogoutCallbackPath("https://echempad-cas.iciq.es:8443/cas");
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }


    @EventListener
    public SingleSignOutHttpSessionListener singleSignOutHttpSessionListener(
            HttpSessionEvent event) {
        return new SingleSignOutHttpSessionListener();
    }


}
