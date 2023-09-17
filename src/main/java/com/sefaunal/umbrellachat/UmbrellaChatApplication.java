package com.sefaunal.umbrellachat;

import com.sefaunal.umbrellachat.Config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class UmbrellaChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmbrellaChatApplication.class, args);
	}

}
