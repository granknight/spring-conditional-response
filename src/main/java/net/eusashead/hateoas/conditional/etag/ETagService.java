package net.eusashead.hateoas.conditional.etag;

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
