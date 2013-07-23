package net.eusashead.hateoas.conditional.etag;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * This class represents 
 * the ETags for a Path that
 * correspond to query string
 * values. This enables a single
 * resource to have multiple ETag
 * values depending on the query string.
 * 
 * This allows for situations like a resource
 * which represents a collection of objects
 * which might have parameters for paging
 * and sorting. Clearly, although it is the
 * same resource, from the client's perspective
 * the data is different and should 
 * have a different ETag. 
 * 
 * @author patrickvk
 *
 */
public class PathETagMap implements Serializable  {
	
	private static final long serialVersionUID = 6099192572372311938L;

	private static final String DEFAULT = "__DEFAULT__";
	
	private final ConcurrentMap<String, String> store = new ConcurrentHashMap<String, String>();

	public String get(String queryString) {
		if (queryString == null) {
			queryString = PathETagMap.DEFAULT;
		}
		return store.get(queryString);
	}
	
	public void put(String queryString, String eTag) {
		if (queryString == null) {
			queryString = PathETagMap.DEFAULT;
		}
		store.put(queryString, eTag);
	}
	
	public void remove(String queryString) {
		if (queryString == null) {
			queryString = PathETagMap.DEFAULT;
		}
		store.remove(queryString);
	}
	
}
