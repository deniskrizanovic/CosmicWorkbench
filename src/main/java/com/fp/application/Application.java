package com.fp.application;

import org.h2.server.web.WebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.fp")
public class Application {

	public static void main(String[] args) throws Throwable {

        System.out.println("you will find the application at http://localhost:8080");
        SpringApplication.run(Application.class, args);
	}

	@Bean
	public ServletRegistrationBean h2servletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(
				new WebServlet());
		registration.addUrlMappings("/console/*");
		return registration;
	}

}
