package uk.ac.ucl.rits.popchat.rhyming;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.ucl.rits.popchat.messages.NewUser;
import uk.ac.ucl.rits.popchat.security.ResourceServerConfiguration;
import uk.ac.ucl.rits.popchat.users.PopUser;
import uk.ac.ucl.rits.popchat.users.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RhymesTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JdbcClientDetailsService service;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepo;

	private static final String CLIENT_NAME = "popchat-tester";
	private static final String CLIENT_PASSWORD = "SuperSecretPassword";

	private static final String ADMIN_USERNAME = "test-admin";
	private static final String ADMIN_PASSWORD = "Super-secret-password";

	private static final String USER_USERNAME = "test-user";
	private static final String USER_PASSWORD = "Very-secret-password";

	/**
	 * This ensures that the setup code to populate database entities only runs once
	 */
	private static boolean setup = true;

	/**
	 * Ensure that we have created everything that we need for the tests in this
	 * file. Specifically this is:
	 * <ul>
	 * <li>Client defined by CLIENT_NAME and CLIENT_PASSWORD
	 * <li>Admin user defined by ADMIN_USERNAME and ADMIN_PASSWORD
	 * <li>Normal user defined by USER_USERNAME and USER_PASSWORD
	 * </ul>
	 * 
	 * Note that internally this method is setup to only run once. Normally, you
	 * would do this using a @BeforeClass annotation. But that requires the method
	 * to be static, so we don't have access to all the @Autowired fields that we
	 * require.
	 */
	@Before
	public void setup() {
		if (!setup) {
			return;
		}
		// Set up a client that is able to access the data
		BaseClientDetails coreClient = new BaseClientDetails(CLIENT_NAME, ResourceServerConfiguration.RESOURCE_ID,
				"read,write", "password,refresh_token", "ROLE_BASIC,ROLE_ADMIN");
		coreClient.setClientSecret(passwordEncoder.encode(CLIENT_PASSWORD));
		service.addClientDetails(coreClient);

		// Set up a basic user
		PopUser user = new PopUser(USER_USERNAME, passwordEncoder.encode(USER_PASSWORD), false);
		userRepo.save(user);

		// Set up an admin user
		PopUser admin = new PopUser(ADMIN_USERNAME, passwordEncoder.encode(ADMIN_PASSWORD), true);
		userRepo.save(admin);

		setup = false;
	}

	/**
	 * Log in using OAuth2 with a given username and password. The login is done
	 * using the test application credentials.
	 * 
	 * @param username The username to log in as
	 * @param password The password for the username
	 * @return Bearer token to use with subsequent queries
	 */
	private String obtainAccessToken(String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("username", username);
		params.add("password", password);

		ResultActions result = mvc
				.perform(post("/oauth/token").params(params).with(httpBasic(CLIENT_NAME, CLIENT_PASSWORD))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}

	/**
	 * Make sure that case doesn't matter in the rhyme lookup
	 */
	@Test
	public void testRhymes() {
		Rhymes rhymes = Rhymes.getRhymes();

		assertNotNull(rhymes);

		String word = "RaQuEl";
		Set<String> matches = rhymes.rhymes(word);
		assertTrue(matches.size() > 0);
		assertFalse(matches.contains(word));
		assertTrue(matches.contains("michelle"));
	}

	/**
	 * Make sure that the rhyming end point is hooked up
	 * 
	 * @throws Exception Something bad happened
	 */
	@Test
	public void rhymesAccessibleToUser() throws Exception {
		String accessToken = obtainAccessToken(USER_USERNAME, USER_PASSWORD);

		MockHttpServletRequestBuilder query = get("/words/rhymes-with/dog").header("Authorization",
				"Bearer " + accessToken);

		ResultActions ra = mvc.perform(query);

		ra.andExpect(status().isOk()).andExpect(
				content().json("[\"epilogue\",\"cog\",\"acog\",\"log\",\"bog\",\"haug\",\"zaugg\",\"blog\",\"fog\"]"));
	}

	/**
	 * Ensure that you cannot access rhymes with without logging in
	 * 
	 * @throws Exception Something bad happened
	 */
	@Test
	public void rhymesEndpointLocked() throws Exception {

		MockHttpServletRequestBuilder query = get("/words/rhymes-with/dog");

		ResultActions ra = mvc.perform(query);

		// Unauthorized because you should be prompted to log in
		ra.andExpect(status().isUnauthorized());
	}

	/**
	 * Make sure that users can't batch create accounts
	 */
	@Test
	public void testUserCantBatchCreate() throws Exception {
		String accessToken = obtainAccessToken(USER_USERNAME, USER_PASSWORD);

		MockHttpServletRequestBuilder query = post("/user/batch-signup").header("Authorization",
				"Bearer " + accessToken);

		ResultActions ra = mvc.perform(query);

		// Forbidden because your user doesn't have permission
		ra.andExpect(status().isForbidden());
	}

	/**
	 * Test admins batch creating accounts. Ensure the right number are created, and
	 * the prefix is honoured.
	 */
	@Test
	public void testAdminCanBatchCreate() throws Exception {
		String accessToken = obtainAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);

		final int numUsers = 5;
		final String prefix = "batch-test-";

		String requestBody = String.format("{ \"numUsers\": %d, \"prefix\": \"%s\"}", numUsers, prefix);

		MockHttpServletRequestBuilder query = post("/user/batch-signup")
				.header("Authorization", "Bearer " + accessToken).header("content-type", "application/json")
				.content(requestBody);

		ResultActions ra = mvc.perform(query);

		ra.andExpect(status().isOk()).andExpect((result) -> {
			String data = result.getResponse().getContentAsString();
			List<NewUser> newUsers = new ObjectMapper().readValue(data, new TypeReference<List<NewUser>>() {
			});
			// Ensure you have the right number
			assert (newUsers.size() == numUsers);
			// Ensure the usernames are right
			newUsers.forEach(user -> {
				assert (user.getUsername().startsWith(prefix));
			});
		});
	}

	/**
	 * Test to make sure that you can list songs in a pageable manner
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSong() throws Exception {
		String accessToken = obtainAccessToken(USER_USERNAME, USER_PASSWORD);

		int perPage = 3;
		MockHttpServletRequestBuilder query = get("/songs").header("Authorization", "Bearer " + accessToken)
				.param("page", "0").param("perPage", String.format("%d", perPage));

		ResultActions ra = mvc.perform(query);

		ra.andExpect(status().isOk()).andExpect((result) -> {
			String data = result.getResponse().getContentAsString();
			@SuppressWarnings("rawtypes")
			Map<String, Object> songs = new ObjectMapper().readValue(data, new TypeReference<HashMap>() {
			});
			System.out.println(songs);
			// Ensure you have the right number
			assert (songs.get("size").equals(perPage));
		});
	}
}
