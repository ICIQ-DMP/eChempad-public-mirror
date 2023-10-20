package org.ICIQ.eChempad.configurations.security;

import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.logging.Logger;

import static org.springframework.security.cas.ServiceProperties.DEFAULT_CAS_ARTIFACT_PARAMETER;

@Component
public class CasSecuredApplication {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(CasAuthenticationProvider casAuthenticationProvider) {
        return new ProviderManager(Arrays.asList(casAuthenticationProvider));
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(
            ServiceProperties serviceProperties, AuthenticationManager authenticationManager) throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setServiceProperties(serviceProperties);
        return filter;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService("http://localhost:8081/login/cas");
        serviceProperties.setSendRenew(false);
        serviceProperties.setArtifactParameter(DEFAULT_CAS_ARTIFACT_PARAMETER);
        return serviceProperties;
    }

    @Bean
    public TicketValidator ticketValidator() {
        return new Cas30ServiceTicketValidator("https://localhost:8443/cas");
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(
            TicketValidator ticketValidator,
            ServiceProperties serviceProperties) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties);
        provider.setTicketValidator(ticketValidator);
        //provider.setUserDetailsService(s -> new User("casuser", "Mellon", true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
        provider.setUserDetailsService(this.userDetailsService);

        //provider.setAuthenticationUserDetailsService(authenticationUserDetailsService());

        provider.setKey("CAS_PROVIDER_LOCALHOST_8081");
        return provider;
    }

    @Bean
    @Autowired
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties)
    {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties);
        // TODO: this url needs to be parametrized for developer / production mode
        casAuthenticationEntryPoint.setLoginUrl("https://localhost:8443/cas/login");
        return casAuthenticationEntryPoint;

    }

    @Bean
    public AuthenticationUserDetailsService<Authentication> authenticationUserDetailsService() {
        UserDetailsByNameServiceWrapper<Authentication> serviceWrapper = new UserDetailsByNameServiceWrapper<>();
        serviceWrapper.setUserDetailsService(this.userDetailsService);
        return serviceWrapper;
    }

}
