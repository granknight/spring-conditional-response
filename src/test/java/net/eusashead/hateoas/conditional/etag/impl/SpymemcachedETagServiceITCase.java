package net.eusashead.hateoas.conditional.etag.impl;

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

import java.io.IOException;

import net.eusashead.hateoas.conditional.service.ETagService;
import net.eusashead.hateoas.conditional.service.impl.SpymemcachedETagService;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SpymemcachedETagServiceITCase extends AbstractETagServiceITCase {

	public ETagService build() throws IOException {
		MemcachedClient c = new MemcachedClient(
		        AddrUtil.getAddresses("localhost:11211"));
		return new SpymemcachedETagService(c);
	}
	
	
}
