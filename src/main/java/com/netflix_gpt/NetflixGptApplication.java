package com.netflix_gpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NetflixGptApplication {

	public static void main(String[] args) {
		System.out.println("ENV URL = " + System.getenv("SPRING_DATASOURCE_URL"));
		SpringApplication.run(NetflixGptApplication.class, args);
	}

}
