package com.sugarcanelabour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SugarCaneLabourmanagmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SugarCaneLabourmanagmentApplication.class, args);
		System.out.println("Server Started ....");
	}

}
