package net.eusashead.hateoas.conditional.interceptor;

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
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.eusashead.hateoas.conditional.etag.ETagService;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * This {@link HandlerInterceptor} enables conditional responses
 * based on ETag, If-Match and If-None-Match headers.
 * 
 * It uses an {@link ETagService} to store the ETags
 * for a given {@link URI} between requests.
 * 
 * GET and HEAD: return a 304 if the 
 * If-None-Match header matches the 
 * stored ETag for the {@link URI}.
 * After the GET or HEAD, the new
 * ETag value is stored.
 * 
 * PUT and DELETE: returns a 428
 * Precondition Required if no
 * If-Match header supplied. Returns
 * a 412 if the If-Match header doesn't
 * match the ETag stored for the {@link URI}
 * After a PUT, the ETag value for the {@link URI}
 * is updated and all previous ETags are evicted.
 * 
 * POST: Removes the ETag for the {@link URI}
 * after the POST has occurred.
 * 
 * @author patrickvk
 *
 */
public class ConditionalResponseInterceptor implements
		HandlerInterceptor {

	// Headers
	private static final String ETAG = "Etag";
	private static final String IF_MATCH = "If-Match";
	private static final String IF_NONE_MATCH = "If-None-Match";
	
	// Verbs
	private static final String POST = "POST";
	private static final String DELETE = "DELETE";
	private static final String PUT = "PUT";
	private static final String HEAD = "HEAD";
	private static final String GET = "GET";
	private final ETagService service;
	
	/**
	 * Pass in the ETagService
	 * that will be used to store
	 * and retrieve ETag values
	 * 
	 * @param service
	 */
	public ConditionalResponseInterceptor(ETagService service) {
		this.service = service;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (request.getMethod().equals(GET)) {
			return preHandleGetOrHead(request, response);
		} else if (request.getMethod().equals(HEAD)) {
			return preHandleGetOrHead(request, response);
		} else if (request.getMethod().equals(PUT)) {
			return preHandlePutOrDelete(request, response);
		} else if (request.getMethod().equals(DELETE)) {
			return preHandlePutOrDelete(request, response);
		}
		return true;
	}


	/**
	 * Return a 304 if the "If-None-Match"
	 * header matches the stored ETag
	 * for the URL.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean preHandleGetOrHead(HttpServletRequest request,
			HttpServletResponse response) {
		String requestETag = request.getHeader(IF_NONE_MATCH);
		String storedETag = service.get(getUri(request));
		if (storedETag != null && requestETag != null && storedETag.equals(requestETag)) {
			response.setStatus(304);
			return false;
		}
		return true;
	}

	/**
	 * Check for an If-Match header
	 * and return a 428 Precondition Required
	 * if not present.
	 * 
	 * Check the If-Match header if supplied
	 * and compare with the ETag for that
	 * resource (if present). 
	 * 
	 * If there is an ETag present and
	 * it is different from the If-Match
	 * header return a 412 Precondition
	 * Failed.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean preHandlePutOrDelete(HttpServletRequest request,
			HttpServletResponse response) {
		
		// Get the If-Match header and the ETag
		String ifMatch = request.getHeader(IF_MATCH);
		String storedETag = service.get(getUri(request));
		
		// Require If-Match
		if (ifMatch == null) {
			// Precondition required, bitch
			response.setStatus(428);
			return false;
		} else if (storedETag == null) {
			// The resource hasn't been GOT
			response.setStatus(412);
			return false;
		} else if (storedETag != null && !storedETag.equals(ifMatch)) {
			// If-Match doesn't match ETag, precondition failed
			response.setStatus(412);
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (request.getMethod().equals(GET)) {
			postHandleGetOrHead(request, response);
		} else if (request.getMethod().equals(HEAD)) {
			postHandleGetOrHead(request, response);
		} else if (request.getMethod().equals(PUT)) {
			postHandlePut(request, response);
		} else if (request.getMethod().equals(DELETE)) {
			postHandleDelete(request);
		} else if (request.getMethod().equals(POST)) {
			postHandlePost(request);
		} 
		
	}

	/**
	 * Store the ETag for
	 * the supplied URL after a
	 * GET or HEAD
	 * 
	 * @param request
	 * @param response
	 */
	private void postHandleGetOrHead(HttpServletRequest request,
			HttpServletResponse response) {
		
		if (response.getHeader(ETAG) != null) {
			service.store(getUri(request), response.getHeader(ETAG));
		}
	}
	
	/**
	 * After putting a resource
	 * update the ETag stored
	 * to the new value
	 * to ensure that clients
	 * are required to re-request
	 * the resource and get the latest
	 * 
	 * NB: Kills all ETags that might
	 * have existed under this path for
	 * different query strings
	 * 
	 * @param request
	 * @param response
	 */
	private void postHandlePut(HttpServletRequest request,
			HttpServletResponse response) {
		service.evictAll(getUri(request));
		if (response.getHeader(ETAG) != null) {
			service.store(getUri(request), response.getHeader(ETAG));
		}
		
	}
	
	/**
	 * After deleting a resource,
	 * remove its ETag from the
	 * store. This will force 
	 * clients to re-request
	 * this resource and get a 404
	 * 
	 * NB: Evicts all ETags
	 * below the path of the URI
	 * for all query strings
	 * 
	 * @param request
	 * @param response
	 */
	private void postHandleDelete(HttpServletRequest request) {
		
		service.evictAll(getUri(request));
		
	}
	
	/**
	 * After a POST request,
	 * remove the ETag for the
	 * URI to force new 
	 * requests to get the
	 * latest version.
	 * 
	 * NB: Evicts all ETags
	 * below the path of the URI
	 * for all query strings
	 * 
	 * @param request
	 * @param response
	 */
	private void postHandlePost(HttpServletRequest request) {
		
		service.evictAll(getUri(request));
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// Don't do anything.
	}
	
	/**
	 * Get the full URI *including*
	 * the query string. That's because
	 * the content of a resource might
	 * be different depending on the query
	 * string, although the actual server side
	 * resource would be the same.
	 * @param request
	 * @return
	 * @throws URISyntaxException 
	 */
	private URI getUri(HttpServletRequest request) {
		StringBuilder requestUri = new StringBuilder();
		requestUri.append(request.getRequestURI());
	    String queryString = request.getQueryString();

	    if (queryString != null) {
	    	requestUri.append('?').append(queryString);
	    }
	    try {
			return new URI(requestUri.toString());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("The request could nt be parsed as a URI.");
		}
	}

}
