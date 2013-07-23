package net.eusashead.hateoas.conditional.etag.impl;

import java.net.URI;
import java.net.URISyntaxException;

import net.eusashead.hateoas.conditional.etag.ETagService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ETagServiceImplTest {
	
	private ETagService service;
	
	@Before
	public void before() {
		service = new ETagServiceImpl(new ConcurrentMapPathETagRepository());
	}
	
	@Test
	public void testUriParse() throws URISyntaxException {
		String uriString = "/path/to/thing/?query=foo&thing=stuff";
		URI uri = new URI(uriString);
		Assert.assertEquals("/path/to/thing/", uri.getPath());
		Assert.assertEquals("query=foo&thing=stuff", uri.getQuery());
	}

	@Test
	public void testStoreAndGetQuery() throws URISyntaxException {
		URI uri = new URI("/path/to/thing/?query=foo&thing=stuff");
		service.store(uri, "etag");
		Assert.assertEquals("etag", service.get(uri));
	}
	
	@Test
	public void testUpdateAndGetQuery() throws URISyntaxException {
		URI uri = new URI("/path/to/thing/?query=foo&thing=stuff");
		service.store(uri, "etag");
		service.store(uri, "etag2");
		Assert.assertEquals("etag2", service.get(uri));
	}
	
	@Test
	public void testEvictAndGetNoQuery() throws URISyntaxException {
		URI uri = new URI("/path/to/thing");
		service.store(uri, "etag");
		Assert.assertEquals("etag", service.get(uri));
		service.evict(uri);
		Assert.assertNull(service.get(uri));
	}
	
	@Test
	public void testStoreAndGetNoQuery() throws URISyntaxException {
		URI uri = new URI("/path/to/thing");
		service.store(uri, "etag");
		Assert.assertEquals("etag", service.get(uri));
	}
	
	@Test
	public void testUpdateAndGetNoQuery() throws URISyntaxException {
		URI uri = new URI("/path/to/thing");
		service.store(uri, "etag");
		service.store(uri, "etag2");
		Assert.assertEquals("etag2", service.get(uri));
	}
	
	@Test
	public void testEvictAndGetQuery() throws URISyntaxException {
		URI uri = new URI("/path/to/thing/?query=foo&thing=stuff");
		service.store(uri, "etag");
		Assert.assertEquals("etag", service.get(uri));
		service.evict(uri);
		Assert.assertNull(service.get(uri));
	}
	
	@Test
	public void testEvictAllAndGetQuery() throws URISyntaxException {
		URI uri1 = new URI("/path/to/thing/?query=foo&thing=stuff");
		URI uri2 = new URI("/path/to/thing/?query=bar&thing=stuff");
		service.store(uri1, "etag1");
		service.store(uri2, "etag2");
		Assert.assertEquals("etag1", service.get(uri1));
		Assert.assertEquals("etag2", service.get(uri2));
		service.evictAll(new URI("/path/to/thing/"));
		Assert.assertNull(service.get(uri1));
		Assert.assertNull(service.get(uri2));
	}
}
