package net.eusashead.hateoas.conditional.service.impl;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.eusashead.hateoas.conditional.service.ETagService;
import net.spy.memcached.MemcachedClient;

public class SpymemcachedETagService extends ETagServiceImpl implements ETagService {

	private final int ttl;
	private final MemcachedClient store;

	public SpymemcachedETagService(MemcachedClient client) {
		this.store = client;
		this.ttl = 3600*24;
	}
	
	public SpymemcachedETagService(MemcachedClient client, int expiryTime) {
		this.store = client;
		this.ttl = expiryTime;
	}

	@Override
	public void store(URI uri, String eTag) {
		assertURI(uri);
		Map<String, String> map = getMap(uri);
		String query = getQuery(uri);
		if (map == null) {
			map = new ConcurrentHashMap<String, String>();
			map.put(query, eTag);
			store.add(uri.getPath(), ttl, map);
		} else {
			map.put(query, eTag);
			store.replace(uri.getPath(), ttl, map);
		}
	}

	@Override
	public String get(URI uri) {
		assertURI(uri);
		String etag = null;
		Map<String, String> map = getMap(uri);
		if (map != null) {
			String query = getQuery(uri);
			etag = map.get(query);
		}
		return etag;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> getMap(URI uri) {
		return (Map<String, String>)store.get(uri.getPath());
	}

	@Override
	public void evictAll(URI uri) {
		assertURI(uri);
		store.delete(uri.getPath());
	}

	@Override
	public void evict(URI uri) {
		Map<String, String> map = getMap(uri);
		if (map != null) {
			String query = getQuery(uri);
			map.remove(query);
			store.replace(uri.getPath(), ttl, map);
		}
	}

}
