package com.millsofmn.example.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.Filter;

@SpringBootApplication
@EnableOAuth2Client
public class Oauth2ExampleApplication extends WebSecurityConfigurerAdapter {

	@Autowired
	private OAuth2ClientContext oauth2ClientContext;
	@Autowired
	private ResourceServerProperties resourceServerProperties;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**")
                // add ssoFilter before BasicAuthenticationFilter
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)

                .authorizeRequests()
                    // add pages that need to be available outside of authentication
                    .antMatchers("/about").permitAll()

                    // everything else will need to authenticated
                    .anyRequest().authenticated()
                .and()
                    // if trying to access resource and not authenticated forward to login
                    .exceptionHandling()
					.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
				.and()
                    // CSRF for Angular
                    .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		;
	}

	private Filter ssoFilter() {
		// setting up filter to be triggered by login page
		OAuth2ClientAuthenticationProcessingFilter filter =
				new OAuth2ClientAuthenticationProcessingFilter("/login");
		OAuth2RestTemplate template = new OAuth2RestTemplate(authorizationCodeResourceDetails(), oauth2ClientContext);
		filter.setRestTemplate(template);

		// register the custom token service
		AdfsUserInfoTokenServices tokenServices = new AdfsUserInfoTokenServices(
		        resourceServerProperties.getUserInfoUri(), resourceServerProperties.getClientId());
		tokenServices.setPrincipalExtractor(new AdfsPrincipalExtractor());
		filter.setTokenServices(tokenServices);

		return filter;
	}

	// load the bean with properties from application properties
	@Bean
	@ConfigurationProperties("security.oauth2.client")
	public AuthorizationCodeResourceDetails authorizationCodeResourceDetails(){
		return new AuthorizationCodeResourceDetails();
	}

	// register the oauth filter to forward unauthenticated users to registration
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter){
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);

		return registration;
	}

	// Spring Boot
	public static void main(String[] args) {
		SpringApplication.run(Oauth2ExampleApplication.class, args);
	}
}
