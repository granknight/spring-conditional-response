Spring Conditional Response
===========================

Spring `HandlerInterceptor` for handling conditional `GET`, `PUT` and `DELETE` requests based on ETag headers in a non-intrusive way.

This Spring MVC `HandlerInterceptor` enables conditional responses based on `ETag`, `If-Match` and `If-None-Match` headers. It uses an `ETagService` to store the ETags for a given URI (path and query string) between requests.

The default implementation stores ETag values for URIs in a `ConcurrentHashMap`, which is obviously not suitable for production use in a cluster. An implementation backed on to Memcached via Spymemcached is also provided and it is simple to create additional implementations (say using Ehcache, Mongo or whatever).
 
GET and HEAD
------------
The intention is to preserve bandwidth by only returning the resource if the version held by the client is not the current version.

The interceptor will return a `304 Not Modified` response in the `preHandle()` method *without* invoking the controller method if the supplied `If-None-Match` header matches the stored ETag for the request URI.

If there is no match, the controller method will be invoked. After the `GET` or `HEAD` controller method returns, the new ETag value is stored in the `ETagService` in the `postHandle()` method.
 
PUT and DELETE
--------------
The intention is to prevent "lost updates" (dirty reads/writes) through concurrent modifications of resources. A modification request must include the version of the resource that the modification was made upon and this version must be the latest version.

To achieve this, the interceptor will return a returns a `428 Precondition Required` (in the preHandle(), method without executing the intercepted controller method) if no `If-Match` header is supplied in the request. The `If-Match` header should contain the `ETag` value that was returned in the `GET` or `HEAD` request for the resource originally.

It will return a `412 Precondition Failed` (in the preHandle(), method without executing the intercepted controller method) if the `If-Match` header doesn't match the `ETag` stored for the URI.

After a `PUT` (in the postHandle() method) the `ETag` value for the URI is updated and all previous `ETag` values are evicted (e.g. the path and any variants based on query strings).

After a `DELETE` (in the postHandle() method) all `ETag` values are removed for this URI (e.g. the path and any variants based on query strings).

POST
----
If a resource receives a POST request, its contents could be modified and clients should re-request the resource.

The interceptor removes the `ETag` for the URI after the `POST` has occurred in the postHande() method (e.g. the path and any variants based on query strings).

A Note on Resources, Paths and Query Strings
----------------------------------
The assumption is that each resource has a consistent, canonical URI which is a path (e.g. /customer/1234 or /customers/). However, a request for a resource might also include a query string (e.g. /customers/?page=0&size=10&sort=name).

The ETag service will store an ETag for any unique combination of path and query string. That is because a query string might modify the data in the returned resource representation. For example, a resource representing a list of customers might have pagination and sort parameters that would alter the data the client receives. Therefore, the ETag needs to cater for this.

Methods which modify the resource (e.g. by PUT, DELETE or POST) need to invalidate the ETags for it and potentially store a new ETag value (not for POST or DELETE).

It's important to note that such a modification will flush the ETags stored for ALL query string variants of a resource, since they are all potentially invalidated by the update.

So a PUT to /customer/1234 would remove ETags stored for URIs /customer/1234 as well as /customer/1234?view=full.
