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

public class ConcurrentMapETagService extends ETagServiceImpl implements ETagService {
	
	private final Map<String, Map<String,String>> store;
	
	public ConcurrentMapETagService() {
		this.store = new ConcurrentHashMap<String, Map<String,String>>();
	}

	@Override
	public void store(URI uri, String eTag) {
		assertURI(uri);
		Map<String, String> map = getMap(uri);
		if (map == null) {
			map = new ConcurrentHashMap<String, String>();
		}
		String query = getQuery(uri);
		map.put(query, eTag);
		store.put(uri.getPath(), map);
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
	
	private Map<String, String> getMap(URI uri) {
		return (Map<String, String>)store.get(uri.getPath());
	}

	@Override
	public void evictAll(URI uri) {
		assertURI(uri);
		store.remove(uri.getPath());
	}

	@Override
	public void evict(URI uri) {
		Map<String, String> map = getMap(uri);
		if (map != null) {
			String query = getQuery(uri);
			map.remove(query);
		}
	}
}
