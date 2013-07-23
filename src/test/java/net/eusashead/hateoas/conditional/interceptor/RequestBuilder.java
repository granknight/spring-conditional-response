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

import javax.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockHttpServletRequest;

public class RequestBuilder {

	private final MockHttpServletRequest request;

	public RequestBuilder(String path, String verb) {

		// Create the request
		this.request = new MockHttpServletRequest();

		// Set verb, path and query
		request.setRequestURI(path);
		request.setMethod(verb);

	}	

	public RequestBuilder(String path, String query, String verb) {

		// Create the request
		this.request = new MockHttpServletRequest();

		// Set verb, path and query
		request.setRequestURI(path);
		request.setQueryString(query);
		request.setMethod(verb);

	}

	public RequestBuilder ifNoneMatch(String ifNoneMatch) {
		this.request.addHeader("If-None-Match", ifNoneMatch);
		return this;
	}

	public RequestBuilder ifMatch(String ifMatch) {
		this.request.addHeader("If-Match", ifMatch);
		return this;
	}

	public HttpServletRequest build() {
		return this.request;
	}

	public static RequestBuilder get(String path) {
		return request(path, "GET");
	}

	public static RequestBuilder get(String path, String query) {
		return request(path, query, "GET");
	}
	
	public static RequestBuilder head(String path) {
		return request(path, "HEAD");
	}

	public static RequestBuilder head(String path, String query) {
		return request(path, query, "HEAD");
	}

	public static RequestBuilder put(String path) {
		return request(path, "PUT");
	}

	public static RequestBuilder put(String path, String query) {
		return request(path, query, "PUT");
	}
	
	public static RequestBuilder delete(String path) {
		return request(path, "DELETE");
	}

	public static RequestBuilder delete(String path, String query) {
		return request(path, query, "DELETE");
	}
	
	public static RequestBuilder post(String path) {
		return request(path, "POST");
	}

	public static RequestBuilder post(String path, String query) {
		return request(path, query, "POST");
	}
	
	public static RequestBuilder request(String path, String verb) {
		return new RequestBuilder(path, verb);
	}

	public static RequestBuilder request(String path, String query, String verb) {
		return new RequestBuilder(path, query, verb);
	}

}
