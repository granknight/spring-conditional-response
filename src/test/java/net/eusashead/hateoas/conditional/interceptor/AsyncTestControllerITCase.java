package net.eusashead.hateoas.conditional.interceptor;

/*
 * #[license]
 * spring-responseentitybuilder
 * %%
 * Copyright (C) 2013 Eusa's Head
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * %[license]
 */

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={WebConfig.class})
public class AsyncTestControllerITCase {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void before() {
		this.mockMvc = webAppContextSetup(this.context).dispatchOptions(true).build();
	}

	@Test
	public void testConditionalGet() throws Exception {

		// Expected result
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"123456\"");
		ResponseEntity<String> expectedResult = new ResponseEntity<String>("hello", headers, HttpStatus.OK);

		// Execute asynchronously
		MvcResult mvcResult = this.mockMvc.perform(get("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(expectedResult))
				.andReturn();

		// Perform asynchronous dispatch
		this.mockMvc.perform(asyncDispatch(mvcResult))
		.andExpect(status().isOk())
		.andExpect(header().string("ETag", "\"123456\""))
		.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))));

		// The 304 should be returned synchronously (it bypasses the controller)
		this.mockMvc.perform(get("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-None-Match", "\"123456\""))
				.andExpect(status().isNotModified())
				.andReturn();


	}

	@Test
	public void testConditionalHead() throws Exception {

		// Expected result
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"123456\"");
		ResponseEntity<String> expectedResult = new ResponseEntity<String>(headers, HttpStatus.OK);

		// Execute asynchronously
		MvcResult mvcResult = this.mockMvc.perform(request(HttpMethod.HEAD, "http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(expectedResult))
				.andReturn();

		// Perform asynchronous dispatch
		this.mockMvc.perform(asyncDispatch(mvcResult))
		.andExpect(status().isOk())
		.andExpect(header().string("ETag", "\"123456\""))
		.andExpect(content().string(""));

		// The 304 should be returned synchronously (it bypasses the controller)
		this.mockMvc.perform(request(HttpMethod.HEAD, "http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-None-Match", "\"123456\""))
				.andExpect(status().isNotModified())
				.andReturn();

	}

	@Test
	public void testPutWithoutIfMatch() throws Exception {

		// The 428 should be returned synchronously (it bypasses the controller)
		this.mockMvc.perform(put("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionRequired())
				.andReturn();

	}

	@Test
	public void testDeleteWithoutIfMatch() throws Exception {

		// The 428 should be returned synchronously (it bypasses the controller)
		this.mockMvc.perform(delete("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionRequired())
				.andReturn();

	}

	@Test
	public void testPutPreconditionFailed() throws Exception {

		// Get the resource (to set the ETag)
		MvcResult mvcResult = this.mockMvc.perform(get("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		// Perform asynchronous dispatch
		this.mockMvc.perform(asyncDispatch(mvcResult))
		.andExpect(status().isOk())
		.andExpect(header().string("ETag", "\"123456\""))
		.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))));


		// The 412 should be returned synchronously (it bypasses the controller)
		this.mockMvc.perform(put("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "2132132"))
				.andExpect(status().isPreconditionFailed())
				.andReturn();

	}

	@Test
	public void testDeletePreconditionFailed() throws Exception {

		// Get the resource (to set the ETag)
		MvcResult mvcResult = this.mockMvc.perform(get("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		// Perform asynchronous dispatch
		this.mockMvc.perform(asyncDispatch(mvcResult))
		.andExpect(status().isOk())
		.andExpect(header().string("ETag", "\"123456\""))
		.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))));


		// The 412 should be returned synchronously (it bypasses the controller)
		this.mockMvc.perform(delete("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "2132132"))
				.andExpect(status().isPreconditionFailed())
				.andReturn();

	}

	@Test
	public void testPutPreconditionOK() throws Exception {

		// Expected result
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"123456\"");
		ResponseEntity<String> expectedResult = new ResponseEntity<String>(headers, HttpStatus.NO_CONTENT);


		// 204 should be returned
		MvcResult mvcResult = this.mockMvc.perform(put("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "\"123456\""))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(expectedResult))
				.andReturn();


		// Perform asynchronous dispatch
		this.mockMvc.perform(asyncDispatch(mvcResult))
		.andExpect(status().isNoContent())
		.andExpect(header().string("ETag", "\"123456\""))
		.andExpect(content().string(""));

	}

	@Test
	public void testDeletePreconditionOK() throws Exception {

		// Expected result
		ResponseEntity<String> expectedResult = new ResponseEntity<String>(HttpStatus.NO_CONTENT);


		// 204 should be returned
		MvcResult mvcResult = this.mockMvc.perform(delete("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "\"123456\""))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(expectedResult))
				.andReturn();

		// Perform asynchronous dispatch
		this.mockMvc.perform(asyncDispatch(mvcResult))
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

	}


	@Test
	public void testPostFlushesETag() throws Exception {

		// Post asynchronously
		MvcResult mvcResult = this.mockMvc.perform(post("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andReturn();

		// Perform asynchronous dispatch
		this.mockMvc.perform(asyncDispatch(mvcResult))
		.andExpect(status().isCreated())
		.andExpect(content().string(""));


		// Expected result
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"123456\"");
		ResponseEntity<String> expectedResult = new ResponseEntity<String>("hello", headers, HttpStatus.OK);

		// Get the resource		
		this.mockMvc.perform(get("http://localhost/async/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(expectedResult))
				.andReturn();

	}

}
