package net.eusashead.hateoas.conditional.cache;

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

import javax.servlet.http.HttpServletResponse;

/**
 * Cache service for returning previously
 * generated {@link HttpServletResponse}
 * to completely circumvent Spring
 * Controller execution.
 * 
 * In order to handle query strings,
 * I think a "cache of caches" is needed.
 * Each cache entry for a URL has a corresponding
 * set of cache entries for each query string
 * variation.
 * 
 * Any PUT, POST or DELETE for a URI would
 * invalidate ALL variations of the resource
 *
 * @author patrickvk
 *
 */
public interface CacheService {
	
	/** 
	 * Store the "default"
	 * {@link HttpServletResponse} for
	 * a URI, e.g. one with no 
	 * query string like /path/to/resource
	 * @param url
	 * @param response
	 */
    void store(String url, HttpServletResponse response);
	
    /** 
	 * Store the {@link HttpServletResponse} 
	 * for a URI with a query string
	 * 
	 * @param url
	 * @param response
	 */
    void store(String url, String query, HttpServletResponse response);

	/**
	 * Get the default response
	 * with no query string
	 * e.g. for /path/to/resource
	 * @param url
	 * @return
	 */
	HttpServletResponse get(String url); 
	
	/**
	 * Get the response
	 * with the query string
	 * e.g. for /path/to/resource?thing=foo
	 * @param url
	 * @return
	 */
	HttpServletResponse get(String url, String query); 

	
	/** 
	 * Remove all variants of 
	 * {@link HttpServletResponse} from
	 * the cache regardless of 
	 * query string
	 * @param url
	 */
	void evictAll(String url);
	
	/**
	 * Clear the entire cache
	 */
	void clear();

}
