package com.nttdata.consumption;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ConsumptionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumptionServiceApplication.class, args);
	}

}
