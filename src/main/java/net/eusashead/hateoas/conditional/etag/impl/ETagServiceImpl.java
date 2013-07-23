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

import net.eusashead.hateoas.conditional.etag.PathETagMap;
import net.eusashead.hateoas.conditional.etag.PathETagRepository;
import net.eusashead.hateoas.conditional.etag.ETagService;

public class ETagServiceImpl implements ETagService {
	
	private final PathETagRepository store; 
	
	public ETagServiceImpl(PathETagRepository store) {
		this.store = store;
	}

	private PathETagMap getMapForPath(String path) {
		PathETagMap map = store.get(path);
		if (map == null) {
			map = new PathETagMap();
			store.store(path, map);
		}
		return map;
	}
	
	private void assertURI(URI uri) {
		if (uri.getPath() == null) {
			throw new IllegalArgumentException("You must provide a path in the URI.");
		}
		
	}

	@Override
	public void store(URI uri, String eTag) {
		assertURI(uri);
		PathETagMap map = getMapForPath(uri.getPath());
		map.put(uri.getQuery(), eTag);
	}


	@Override
	public String get(URI uri) {
		assertURI(uri);
		PathETagMap map = getMapForPath(uri.getPath());
		return map.get(uri.getQuery());
	}

	@Override
	public void evictAll(URI uri) {
		assertURI(uri);
		store.remove(uri.getPath());
	}

	@Override
	public void evict(URI uri) {
		assertURI(uri);
		PathETagMap map = getMapForPath(uri.getPath());
		map.remove(uri.getQuery());
	}
	
}
