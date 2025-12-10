package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ProyectoMarcosDesarrolloWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoMarcosDesarrolloWebApplication.class, args);
	}
}
