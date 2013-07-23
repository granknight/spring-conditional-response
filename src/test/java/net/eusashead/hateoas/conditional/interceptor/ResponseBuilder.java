package net.eusashead.hateoas.conditional.interceptor;

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
