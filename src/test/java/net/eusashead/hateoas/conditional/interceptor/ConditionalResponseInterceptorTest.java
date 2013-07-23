package net.eusashead.hateoas.conditional.interceptor;

import static net.eusashead.hateoas.conditional.interceptor.RequestBuilder.delete;
import static net.eusashead.hateoas.conditional.interceptor.RequestBuilder.get;
import static net.eusashead.hateoas.conditional.interceptor.RequestBuilder.head;
import static net.eusashead.hateoas.conditional.interceptor.RequestBuilder.post;
import static net.eusashead.hateoas.conditional.interceptor.RequestBuilder.put;
import static net.eusashead.hateoas.conditional.interceptor.ResponseBuilder.response;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.eusashead.hateoas.conditional.etag.ETagService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.web.servlet.ModelAndView;

@RunWith(JUnit4.class)
public class ConditionalResponseInterceptorTest {

	private ConditionalResponseInterceptor interceptor;
	private ETagService service;

	@Before
	public void before() {
		service = mock(ETagService.class);
		interceptor = new ConditionalResponseInterceptor(service);
	}

	@Test
	public void testPreHandleGetNotModified() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";
		String etag = "etag";

		// Set ETag value to return by service
		setETag(path, query, etag);

		// Set the request/response
		HttpServletRequest request = get(path, query).ifNoneMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(304, response.getStatus());

	}

	@Test
	public void testPreHandleGetEtagMiss() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";

		// Set ETag value to return by service
		setETag(path, query, "etag-a");

		// Build request/response
		HttpServletRequest request = get("/path/to/resource/", "query=string")
				.ifNoneMatch("etag-b").build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertTrue(handle);

	}

	@Test
	public void testPreHandleGetNoIfNoneMatch() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";

		// Set ETag value to return by service
		setETag(path, query, "etag-a");

		// Build request
		HttpServletRequest request = get(path, query).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertTrue(handle);
	}

	@Test
	public void testPreHandleHeadNotModified() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";
		String etag = "etag";

		// Set ETag value to return by service
		setETag(path, query, etag);

		// Set the request 
		HttpServletRequest request = head(path, query).ifNoneMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(304, response.getStatus());

	}

	@Test
	public void testPreHandleHeadEtagMiss() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";

		// Set ETag value to return by service
		setETag(path, query, "etag-a");

		// Build request
		HttpServletRequest request = head("/path/to/resource/", "query=string")
				.ifNoneMatch("etag-b").build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertTrue(handle);

	}

	@Test
	public void testPreHandleHeadNoIfNoneMatch() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";

		// Set ETag value to return by service
		setETag(path, query, "etag-a");

		// Build request
		HttpServletRequest request = head(path, query).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertTrue(handle);
	}

	@Test
	public void testPostHandleGetWithETag() throws Exception {
		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";
		String etag = "etag";
		
		// Request/Response
		HttpServletRequest request = get(path, query).build();
		HttpServletResponse response = response().etag(etag).build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verify(service).store(createURI(path, query), etag);

	}
	
	@Test
	public void testPostHandleGetWithNoETag() throws Exception {
		
		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";
		
		// Request/response
		HttpServletRequest request = get(path, query).build();
		HttpServletResponse response = response().build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verifyZeroInteractions(service);

	}
	
	@Test
	public void testPostHandleHeadWithETag() throws Exception {
		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";
		String etag = "etag";
		
		// Request/Response
		HttpServletRequest request = head(path, query).build();
		HttpServletResponse response = response().etag(etag).build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verify(service).store(createURI(path, query), etag);

	}
	
	@Test
	public void testPostHandleHeadWithNoETag() throws Exception {
		
		// Request value
		String path = "/path/to/resource/";
		String query = "query=string";
		
		// Request/response
		HttpServletRequest request = head(path, query).build();
		HttpServletResponse response = response().build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verifyZeroInteractions(service);

	}
	
	@Test
	public void testPreHandlePutPreconditionRequired() throws Exception {

		// Request value
		String path = "/path/to/resource/";

		// Set the request/response
		HttpServletRequest request = put(path).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(428, response.getStatus());

	}
	
	@Test
	public void testPreHandlePutPreconditionFailedNoStoredETag() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String etag = "etag";

		// Set the request/response
		HttpServletRequest request = put(path).ifMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(412, response.getStatus());
		verify(service).get(createURI(path));

	}
	
	@Test
	public void testPreHandlePutPreconditionFailedETagIfMatchNoMatch() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String etag = "etag-a";
		
		// Set ETag value to return by service
		setETag(path, null, "etag-b");

		// Set the request/response
		HttpServletRequest request = put(path).ifMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(412, response.getStatus());
		verify(service).get(createURI(path));

	}
	
	@Test
	public void testPreHandlePutPreconditionOK() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String etag = "etag";
		
		// Set ETag value to return by service
		setETag(path, null, etag);

		// Set the request/response
		HttpServletRequest request = put(path).ifMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertTrue(handle);
		verify(service).get(createURI(path));

	}
	
	@Test
	public void testPostHandlePutWithETag() throws Exception {
		
		// Request value
		String path = "/path/to/resource/";
		String etag = "etag";
		
		// Request/Response
		HttpServletRequest request = put(path).build();
		HttpServletResponse response = response().etag(etag).build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verify(service).evictAll(createURI(path));
		verify(service).store(createURI(path), etag);

	}
	
	@Test
	public void testPostHandlePutNoETag() throws Exception {
		
		// Request value
		String path = "/path/to/resource/";
		
		// Request/Response
		HttpServletRequest request = put(path).build();
		HttpServletResponse response = response().build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verify(service).evictAll(createURI(path));
		verifyNoMoreInteractions(service);

	}
	
	@Test
	public void testPreHandleDeletePreconditionRequired() throws Exception {

		// Request value
		String path = "/path/to/resource/";

		// Set the request/response
		HttpServletRequest request = delete(path).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(428, response.getStatus());

	}
	
	@Test
	public void testPreHandleDeletePreconditionFailedNoStoredETag() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String etag = "etag";

		// Set the request/response
		HttpServletRequest request = delete(path).ifMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(412, response.getStatus());
		verify(service).get(createURI(path));

	}
	
	@Test
	public void testPreHandleDeletePreconditionFailedETagIfMatchNoMatch() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String etag = "etag-a";
		
		// Set ETag value to return by service
		setETag(path, null, "etag-b");

		// Set the request/response
		HttpServletRequest request = delete(path).ifMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertFalse(handle);
		Assert.assertEquals(412, response.getStatus());
		verify(service).get(createURI(path));

	}
	
	@Test
	public void testPreHandleDeletePreconditionOK() throws Exception {

		// Request value
		String path = "/path/to/resource/";
		String etag = "etag";
		
		// Set ETag value to return by service
		setETag(path, null, etag);

		// Set the request/response
		HttpServletRequest request = delete(path).ifMatch(etag).build();
		HttpServletResponse response = response().build();

		// Execute
		boolean handle = interceptor.preHandle(request, response, new Object());

		// Validate
		Assert.assertTrue(handle);
		verify(service).get(createURI(path));

	}
	
	@Test
	public void testPostHandleDelete() throws Exception {
		
		// Request value
		String path = "/path/to/resource/";
		
		// Request/Response
		HttpServletRequest request = delete(path).build();
		HttpServletResponse response = response().build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verify(service).evictAll(createURI(path));
		verifyNoMoreInteractions(service);

	}
	
	@Test
	public void testPostHandlePost() throws Exception {
		
		// Request value
		String path = "/path/to/resource/";
		
		// Request/Response
		HttpServletRequest request = post(path).build();
		HttpServletResponse response = response().build();
		
		// Execute
		interceptor.postHandle(request, response, new Object(), new ModelAndView());
		
		// Validate
		verify(service).evictAll(createURI(path));
		verifyNoMoreInteractions(service);

	}
	

	private void setETag(String path, String query, String etag)
			throws URISyntaxException {

		// Create URI
		URI uri = createURI(path, query);

		// Set the ETag to be returned by the service
		doReturn(etag).when(service).get(uri);
	}

	private URI createURI(String path) throws URISyntaxException {
		return createURI(path, null);
	}
	private URI createURI(String path, String query) throws URISyntaxException {
		
		// Make full string
		StringBuilder fullURI = new StringBuilder(path);
		if (query != null) {
			fullURI.append("?");
			fullURI.append(query);
		}

		// Create URI
		URI uri = new URI(fullURI.toString());
		return uri;
	}
}
