package net.eusashead.hateoas.conditional.etag.impl;

import java.io.IOException;

import net.eusashead.hateoas.conditional.etag.PathETagMap;
import net.eusashead.hateoas.conditional.etag.PathETagRepository;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SpymemcachedPathETagRepositoryTest {
	
	private PathETagRepository service;
	
	private MemcachedClient c;

	@Before
	public void before() throws IOException {
		c = new MemcachedClient(
		        AddrUtil.getAddresses("localhost:11211"));
		service = new SpymemcachedPathETagRepository(c);
	}
	
	@Test
	public void testStoreAndGet() {
		PathETagMap etag = createPathETagMap();
		service.store("path", etag);
		PathETagMap stored = service.get("path");
		Assert.assertNotNull(stored);
		Assert.assertEquals("etag", stored.get("query"));
	}

	private PathETagMap createPathETagMap() {
		PathETagMap etag = new PathETagMap();
		etag.put("query", "etag");
		return etag;
	}
	
	@Test
	public void testUpdateAndGet() {
		PathETagMap etag1 = createPathETagMap();
		PathETagMap etag2 = createPathETagMap();
		etag2.put("query", "etag2");
		service.store("path", etag1);
		service.store("path", etag2);
		PathETagMap stored = service.get("path");
		Assert.assertNotNull(stored);
		Assert.assertEquals("etag2", stored.get("query"));
	}
	
	@Test
	public void testEvictAndGetNoQuery() {
		PathETagMap etag = createPathETagMap();
		service.store("path", etag);
		PathETagMap stored = service.get("path");
		Assert.assertNotNull(stored);
		service.remove("path");
		Assert.assertNull(service.get("path"));
	}
	
}
