package com.mgt.rural;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.mgt")
@EntityScan(basePackages = {"com.mgt"})
@EnableAsync
public class RuralApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RuralApplication.class, args);
	}

}
