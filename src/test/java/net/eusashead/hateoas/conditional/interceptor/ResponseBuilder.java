package net.eusashead.hateoas.conditional.interceptor;

/*
 * #[license]
 * spring-conditional-response
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

import javax.servlet.http.HttpServletResponse;

import org.springframework.mock.web.MockHttpServletResponse;

public class ResponseBuilder {
	
	private final MockHttpServletResponse response;
	
	public ResponseBuilder() {
		this.response = new MockHttpServletResponse();
	}
	
	public ResponseBuilder etag(String etag) {
		this.response.addHeader("ETag", etag);
		return this;
	}
	
	public HttpServletResponse build() {
		return this.response;
	}
	
	public static ResponseBuilder response() {
		return new ResponseBuilder();
	}

}
