package com.sefaunal.umbrellaauth;

import com.sefaunal.umbrellaauth.Config.RSA256Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableConfigurationProperties(RSA256Keys.class)
@SpringBootApplication
@EnableFeignClients
public class UmbrellaAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmbrellaAuthApplication.class, args);
	}

}
