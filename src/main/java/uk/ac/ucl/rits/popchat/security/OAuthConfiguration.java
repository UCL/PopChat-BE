package uk.ac.ucl.rits.popchat.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Configure the OAuth with mostly default settings
 * 
 * @author RSDG
 *
 */
@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(SecurityConfiguration.class)
public class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {

	/**
	 * This is the database connection
	 */
	@Autowired
	private DataSource dataSource;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder oauthClientPasswordEncoder;
	@Autowired
	private JdbcClientDetailsService jdbcClientDetailsService;

	/**
	 * This is an sql table creation script to be run on startup to ensure that the
	 * OAuth tables exist
	 */
	@Value("classpath:schema.sql")
	private Resource schemaScript;

	/**
	 * Store the OAuth tokens and credentials in the database
	 * @return TokenStore backed by our database
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	/**
	 * Use the default access denied handler
	 * @return Access Denied handler
	 */
	@Bean
	public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}

	/**
	 * Create a client manager (for OAuth client applications) that is backed by the
	 * database
	 * 
	 * @return The Client Application manager
	 */
	@Bean
	public JdbcClientDetailsService jdbcClientDetailsService() {
		return new JdbcClientDetailsService(dataSource);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
		// Ensure that our password encoder is used for the OAuth
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
				.passwordEncoder(oauthClientPasswordEncoder);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// Set a particular Client (application) manager that lets us add them nicely at
		// runtime
		clients.withClientDetails(jdbcClientDetailsService);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		// Set up the normal Oauth, but using our specific set up for databases etc
		endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService);
	}

	/**
	 * Create a DataSource initialiser that will ensure that we have the OAuth
	 * tables present in the database
	 * 
	 * @param dataSource The database to ensure the tables are present in
	 * @return The DataSourceInitializer
	 */
	@Bean
	public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator());
		return initializer;
	}

	/**
	 * Create a DatabasePopulator that initializes the database with the content of
	 * the schemaScript field
	 * 
	 * @return The DatabasePopulator
	 */
	private DatabasePopulator databasePopulator() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(schemaScript);
		return populator;
	}

}
