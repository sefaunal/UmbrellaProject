package com.sefaunal.umbrellachat;

import com.sefaunal.umbrellachat.Config.RSA256Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RSA256Keys.class)
@SpringBootApplication
public class UmbrellaChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmbrellaChatApplication.class, args);
	}

}
