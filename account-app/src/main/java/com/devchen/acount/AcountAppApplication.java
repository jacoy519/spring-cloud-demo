package com.devchen.acount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AcountAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcountAppApplication.class, args);
	}
}
