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
