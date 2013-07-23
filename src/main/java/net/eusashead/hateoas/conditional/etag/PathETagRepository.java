package net.eusashead.hateoas.conditional.etag;

public interface PathETagRepository {

	PathETagMap get(String path);

	void store(String path, PathETagMap map);

	void remove(String path);

}
