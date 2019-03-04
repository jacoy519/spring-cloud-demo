package com.devchen.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
//@EnableScheduling
=======
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5
public class SpiderAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpiderAppApplication.class, args);
	}
<<<<<<< HEAD


	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
=======
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5
}
