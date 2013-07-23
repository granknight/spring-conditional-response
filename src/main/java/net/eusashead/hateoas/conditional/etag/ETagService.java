package net.eusashead.hateoas.conditional.etag;

import java.net.URI;

/**
 * Implementations of this interface
 * enable storing of ETag values
 * for a URI (which could contain both
 * a path and a query string).
 * 
 * @author patrickvk
 *
 */
public interface ETagService {
	
	/**
	 * Store an ETag for 
	 * the supplied URI which
	 * must have a path and 
	 * might have a query string.
	 * 
	 * @param path a {@link URI} consisting of
	 * a path (required) and a query (optional)
	 * @param eTag {@link String} to store
	 */
	void store(URI uri, String eTag);
	
	
	
	/**
	 * Get the ETag
	 * mapped to this URI
	 * 
	 * @param path a {@link URI} consisting of
	 * a path (required) and a query (optional)
	 * @return {@link String} ETag value
	 */
	String get(URI uri);


	/**
	 * Remove all ETags
	 * for this path,
	 * regardless of query string
	 * 
	 * For example, evictAll(new URI("/path/"))
	 * would flush ETags for 
	 *  /path/
	 *  /path/?query=foo
	 * 
	 * @param path a {@link URI} consisting of
	 * a path (required) and a query (ignored)
	 */
	void evictAll(URI uri);
	
	/**
	 * Remove an ETag
	 * for this URI, which might
	 * include a query string
	 * 
	 * For example, evict(new URI("/path/?query=foo"))
	 * would flush ETag for 
	 *  /path/?query=foo
	 * but would leave untouched
	 *  /path/
	 *  
	 * @param path a {@link URI} consisting of
	 * a path (required) and a query (optional)
	 */
	void evict(URI uri);

}
