package net.eusashead.hateoas.conditional.service.impl;

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

import net.eusashead.hateoas.conditional.service.ETagService;

public abstract class ETagServiceImpl implements ETagService {
	
	protected void assertURI(URI uri) {
		if (uri.getPath() == null) {
			throw new IllegalArgumentException("You must provide a path in the URI.");
		}
		
	}
	
	protected String getQuery(URI uri) {
		String query = uri.getQuery() != null ? uri.getQuery() : "";
		return query;
	}
	
}
