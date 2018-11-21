package uk.ac.ucl.rits.popchat.security;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import uk.ac.ucl.rits.popchat.security.hash.HashUtil;
import uk.ac.ucl.rits.popchat.security.hash.Pbkdf2PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final static Logger log = Logger.getLogger(SecurityConfiguration.class);

	@Autowired
	private PopUserDetailsService userDetailsService;

	@Value("${hash.algorithm}")
	private String defaultEncoder;

	@Value("${salt.algorithm}")
	private String defaultSaltAlgorithm;

	@Value("${salt.length}")
	private int defaultSaltLength;

	@Value("${hash.length}")
	private int defaultHashLength;

	@Value("${hash.iterations}")
	private int defaultHashIterations;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.authProvider());
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/user/signup").permitAll()
		.antMatchers("/oauth/**").permitAll()
		.antMatchers("/actuator/**").permitAll()
		.anyRequest().hasRole("BASIC")
		.and().csrf().disable();
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		try {
			encoders.put(HashUtil.PBKDF2, new Pbkdf2PasswordEncoder(this.defaultSaltAlgorithm, this.defaultSaltLength,
					this.defaultHashLength, this.defaultHashIterations));
		} catch (NoSuchAlgorithmException e) {
			SecurityConfiguration.log.error("Failed create password encoder", e);
			throw new IllegalStateException("Cannnot create password encoder");
		}
		return new DelegatingPasswordEncoder(defaultEncoder, encoders);
	}

}