package uk.ac.ucl.rits.popchat.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * Configures the resource server. This is the notional entity being access by
 * the OAuth2. In our case, this is the REST API we provide.
 *
 * @author RSDG
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * Resource id for this server.
     */
    public static final String RESOURCE_ID = "resource-server-rest-api";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        // Set the resource id for this application
        resources.resourceId(RESOURCE_ID);
    }

    /**
     * This specifies what the permissions for the different end points are.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Spring CORS security problem
                .antMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll().antMatchers("/user/signup").permitAll()
                .antMatchers("/user/batch-signup").hasRole("ADMIN")
                .antMatchers("/user/list").hasRole("ADMIN")
                .antMatchers("/user/promote").hasRole("ADMIN")
                .antMatchers("/user/results").hasRole("ADMIN")
                // Make these endpoints public for debugging.
                // Debug - Actuator
                .antMatchers("/actuator/**").permitAll()
                // Debug - Swagger
                .antMatchers("/swagger-ui.html").permitAll().antMatchers("/webjars/**").permitAll()
                .antMatchers("/null/**").permitAll().antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                // Normal endpoints require any authentication
                .anyRequest().fullyAuthenticated()
                // Debug - Disable CSRF for testing with postman for debugging
                .and().cors().and().csrf().disable();
    }
}
