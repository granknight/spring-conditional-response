package net.eusashead.hateoas.conditional.etag.impl;

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
