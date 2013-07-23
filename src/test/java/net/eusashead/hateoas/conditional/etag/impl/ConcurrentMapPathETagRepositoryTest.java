package net.eusashead.hateoas.conditional.etag.impl;

import net.eusashead.hateoas.conditional.etag.PathETagMap;
import net.eusashead.hateoas.conditional.etag.PathETagRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ConcurrentMapPathETagRepositoryTest {
	
	private PathETagRepository service;
	
	@Before
	public void before() {
		service = new ConcurrentMapPathETagRepository();
	}
	
	@Test
	public void testStoreAndGet() {
		PathETagMap etag = new PathETagMap();
		service.store("path", etag);
		Assert.assertEquals(etag, service.get("path"));
	}
	
	@Test
	public void testUpdateAndGet() {
		PathETagMap etag1 = new PathETagMap();
		PathETagMap etag2 = new PathETagMap();
		service.store("path", etag1);
		service.store("path", etag2);
		Assert.assertEquals(etag2, service.get("path"));
	}
	
	@Test
	public void testEvictAndGetNoQuery() {
		PathETagMap etag = new PathETagMap();
		service.store("path", etag);
		Assert.assertEquals(etag, service.get("path"));
		service.remove("path");
		Assert.assertNull(service.get("path"));
	}
	
}
