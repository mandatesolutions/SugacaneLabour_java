package com.sugarcanelabour.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	
	 @Bean
	    public OpenAPI customOpenAPI() {
	        return new OpenAPI()
	                .info(new Info()
	                        .title("SugarCane Labor Management API")
	                        .description("API documentation for the SugarCane Labor Management Application")
	                        .version("1.0.0"));
	    }

}
