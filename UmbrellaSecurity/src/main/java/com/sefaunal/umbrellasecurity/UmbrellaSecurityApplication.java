package com.sefaunal.umbrellasecurity;

import com.sefaunal.umbrellasecurity.Config.RSA256Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RSA256Keys.class)
@SpringBootApplication
public class UmbrellaSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmbrellaSecurityApplication.class, args);
	}

}
