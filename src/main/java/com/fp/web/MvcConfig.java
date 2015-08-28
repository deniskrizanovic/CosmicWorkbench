package com.fp.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

		registry.addViewController("/").setViewName("index");

		registry.addViewController("/index").setViewName("index");
		registry.addViewController("/system-context").setViewName("system-context");
		registry.addViewController("/create-new-system-context").setViewName("create-new-system-context");
		registry.addViewController("/show-system-context").setViewName("create-new-system-context");

		registry.addViewController("/define-data-groups").setViewName("define-data-groups");
		registry.addViewController("/disp-data-groups").setViewName("define-data-groups");
		registry.addViewController("/show-data-groups").setViewName("define-data-groups");
		registry.addViewController("/create-new-data-group").setViewName("define-data-groups");
        registry.addViewController("/list-of-data-groups").setViewName("list-of-data-groups");

		registry.addViewController("/define-functional-processes").setViewName("define-functional-processes");
		registry.addViewController("/disp-functional-processes").setViewName("define-functional-processes");
		registry.addViewController("/show-functional-processes").setViewName("define-functional-processes");
		registry.addViewController("/create-new-functional-process").setViewName("define-functional-processes");

        registry.addViewController("/grid-model-window").setViewName("grid-model-window");
        registry.addViewController("/define-functional-model").setViewName("define-functional-model");


	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}

}
