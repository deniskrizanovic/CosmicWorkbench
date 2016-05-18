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
        registry.addViewController("/save-data-group").setViewName("define-data-groups");

        registry.addViewController("/define-functional-process").setViewName("define-functional-processes");
        registry.addViewController("/save-functional-process").setViewName("define-functional-processes");

//        registry.addViewController("/grid-model-window").setViewName("grid-model-window");
        registry.addViewController("/define-functional-model").setViewName("define-functional-model");
        registry.addViewController("/select-data-attributes").setViewName("select-data-attributes");
        registry.addViewController("/save-data-attributes").setViewName("select-data-attributes");
        registry.addViewController("/add-datagroup-and-attributes").setViewName("add-datagroup-and-attributes");
//        registry.addViewController("/render-functional-model").setViewName("define-functional-model");


    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

}
