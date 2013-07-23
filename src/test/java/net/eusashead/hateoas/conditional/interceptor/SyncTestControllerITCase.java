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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={WebConfig.class})
public class SyncTestControllerITCase {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void before() {
		this.mockMvc = webAppContextSetup(this.context).dispatchOptions(true).build();
	}

	@Test
	public void testConditionalGet() throws Exception {

		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
				.andReturn();
		
		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-None-Match", "\"123456\""))
				.andExpect(status().isNotModified())
				.andExpect(content().string(""))
				.andReturn();
	}
	
	@Test
	public void testConditionalHead() throws Exception {

		this.mockMvc
		.perform(request(HttpMethod.HEAD, "http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().string(""))
				.andReturn();
		
		this.mockMvc
		.perform(request(HttpMethod.HEAD, "http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-None-Match", "\"123456\""))
				.andExpect(status().isNotModified())
				.andExpect(content().string(""))
				.andReturn();
	}
	
	@Test
	public void testPutWithoutIfMatch() throws Exception {

		this.mockMvc
		.perform(put("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionRequired())
				.andExpect(content().string(""))
				.andReturn();
		
		
	}
	
	@Test
	public void testDeleteWithoutIfMatch() throws Exception {

		this.mockMvc
		.perform(delete("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionRequired())
				.andExpect(content().string(""))
				.andReturn();
		
		
	}
	
	@Test
	public void testPutPreconditionFailed() throws Exception {

		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
				.andReturn();
		
		this.mockMvc
		.perform(put("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "\"765432\"")) // Invalid If-Match, doesn't match ETag above
				.andExpect(status().isPreconditionFailed())
				.andExpect(content().string(""))
				.andReturn();
	}
	
	@Test
	public void testDeletePreconditionFailed() throws Exception {

		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
				.andReturn();
		
		this.mockMvc
		.perform(delete("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "\"765432\"")) // Invalid If-Match, doesn't match ETag above
				.andExpect(status().isPreconditionFailed())
				.andExpect(content().string(""))
				.andReturn();
	}
	
	@Test
	public void testPutPreconditionOK() throws Exception {

		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
				.andReturn();
		
		this.mockMvc
		.perform(put("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "\"123456\"")) // Valid If-Match, matches ETag above
				.andExpect(status().isNoContent())
				.andExpect(content().string(""))
				.andReturn();
	}
	
	@Test
	public void testDeletePreconditionOK() throws Exception {

		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
				.andReturn();
		
		this.mockMvc
		.perform(delete("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("If-Match", "\"123456\"")) // Valid If-Match, matches ETag above
				.andExpect(status().isNoContent())
				.andExpect(content().string(""))
				.andReturn();
	}
	
	@Test
	public void testPostFlushesETag() throws Exception {

		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
				.andReturn();
		
		this.mockMvc
		.perform(post("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				//.header("If-Match", "\"123456\"")) // I don't think conditional posts are necessary
				.andExpect(status().isCreated())
				.andExpect(content().string(""))
				.andReturn();
		
		this.mockMvc
		.perform(get("http://localhost/sync/123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"123456\""))
				.andExpect(content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
				.andReturn();
	}
	
}
