package net.eusashead.hateoas.conditional.etag.impl;

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
