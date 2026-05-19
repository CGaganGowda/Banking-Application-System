package com.Bank.app;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Banking Management System",
				description = "Banking service with simple and secure API's",
				version = "v1.0",
				contact = @Contact(
						name = "Gagan",
						email = "gagancgowda971@gmail.com"
				),
				license = @License(
						name = "Apache 1.0"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Banking Documentation",
				url = "#"
		)
)
@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
}
