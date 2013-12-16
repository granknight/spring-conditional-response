package net.eusashead.hateoas.conditional.etag.impl;

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

import java.net.URI;
import java.net.URISyntaxException;

import net.eusashead.hateoas.conditional.service.ETagService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractETagServiceITCase {
	
	private ETagService service;
	
	public abstract ETagService build() throws Exception;
	
	@Before
	public void before() throws Exception {
		service = build();
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
