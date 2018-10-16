package uk.ac.ucl.rits.popchat.rhyming;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RhymesTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void testRhymes() {
		Rhymes rhymes = Rhymes.getRhymes();

		assertNotNull(rhymes);

		String word = "Raquel";
		Set<String> matches = rhymes.rhymes(word);
		assertTrue(matches.size() > 0);
		assertFalse(matches.contains(word));
		assertTrue(matches.contains("michelle"));
	}

	@Test
	public void testApp() throws Exception {

		MockHttpServletRequestBuilder query = MockMvcRequestBuilders.get("/hello");

		ResultActions ra = mvc.perform(query);

		assertTrue(ra.andReturn().getResponse().getStatus() == 200);

		ra.andExpect(status().isOk()).andExpect(content().string(equalTo("Hello world")));
	}
}
