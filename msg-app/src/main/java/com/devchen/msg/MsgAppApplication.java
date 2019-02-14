package com.devchen.msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient
public class MsgAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsgAppApplication.class, args);
	}
}
