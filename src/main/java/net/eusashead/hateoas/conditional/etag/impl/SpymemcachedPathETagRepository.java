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
import net.spy.memcached.MemcachedClient;

public class SpymemcachedPathETagRepository implements PathETagRepository {

	private final int ttl;
	private final MemcachedClient store;

	public SpymemcachedPathETagRepository(MemcachedClient client) {
		this.store = client;
		this.ttl = 3600*24;
	}
	
	public SpymemcachedPathETagRepository(MemcachedClient client, int expiryTime) {
		this.store = client;
		this.ttl = expiryTime;
	}

	@Override
	public PathETagMap get(String path) {
		assertPath(path);
		return PathETagMap.class.cast(store.get(path));
	}

	@Override
	public void store(String path, PathETagMap map) {
		assertPath(path);
		assertMap(map);
		store.set(path, ttl, map);

	}

	@Override
	public void remove(String path) {
		assertPath(path);
		store.delete(path);
	}

	private void assertPath(String path) {
		if (path == null) {
			throw new IllegalArgumentException("Path cannot be null");
		}
	}

	private void assertMap(PathETagMap map) {
		if (map == null) {
			throw new IllegalArgumentException("ETagMap cannot be null");
		}
	}


}
