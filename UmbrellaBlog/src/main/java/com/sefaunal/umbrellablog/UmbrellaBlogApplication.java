package com.sefaunal.umbrellablog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UmbrellaBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmbrellaBlogApplication.class, args);
	}

}
