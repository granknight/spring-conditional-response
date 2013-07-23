package net.eusashead.hateoas.conditional.etag.impl;

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
