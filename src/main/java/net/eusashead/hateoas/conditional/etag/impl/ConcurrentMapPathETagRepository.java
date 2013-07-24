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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.eusashead.hateoas.conditional.etag.PathETagMap;
import net.eusashead.hateoas.conditional.etag.PathETagRepository;

public class ConcurrentMapPathETagRepository implements PathETagRepository {
	
	private final ConcurrentMap<String, PathETagMap> store;
	
	public ConcurrentMapPathETagRepository() {
		this.store = new ConcurrentHashMap<String, PathETagMap>();
	}

	@Override
	public PathETagMap get(String path) {
		assertPath(path);
		return store.get(path);
	}

	@Override
	public void store(String path, PathETagMap map) {
		assertPath(path);
		assertMap(map);
		store.put(path, map);
	}

	@Override
	public void remove(String path) {
		assertPath(path);
		store.remove(path);
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
