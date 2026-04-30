package com.netflix_gpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NetflixGptApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetflixGptApplication.class, args);
		System.out.println("URL=" + System.getenv("SPRING_DATASOURCE_URL"));
		System.out.println("USER=" + System.getenv("SPRING_DATASOURCE_USERNAME"));

	}

}
