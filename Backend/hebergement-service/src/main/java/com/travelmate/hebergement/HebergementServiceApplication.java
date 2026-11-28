package com.travelmate.hebergement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HebergementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HebergementServiceApplication.class, args);
	}

}
