package net.eusashead.hateoas.conditional.interceptor;

/*
 * #[license]
 * spring-responseentitybuilder
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

import net.eusashead.hateoas.conditional.service.ETagService;
import net.eusashead.hateoas.conditional.service.impl.ConcurrentMapETagService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


@Configuration
@ComponentScan(basePackageClasses=SyncTestController.class)
public class WebConfig extends WebMvcConfigurationSupport {
	
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ConditionalResponseInterceptor(eTagService()));
		super.addInterceptors(registry);
	}
	
	@Bean
	public ETagService eTagService() {
		return new ConcurrentMapETagService();
	}

}
