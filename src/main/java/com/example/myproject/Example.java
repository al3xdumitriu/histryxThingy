package com.example.myproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myproject.DBEntities.DBCustomer;
import com.example.myproject.service.CustomerService;

@EnableCircuitBreaker
@RestController
@SpringBootApplication
@ComponentScan("com.example.myproject")
public class Example {

	@Autowired
	CustomerService customerService;

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	public static void main(String[] args) throws Exception {

		SpringApplication.run(Example.class, args);
	}

}