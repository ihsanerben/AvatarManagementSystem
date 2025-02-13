package com.ihsan.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("com.ihsan")
@EntityScan("com.ihsan")
@EnableJpaRepositories("com.ihsan")
@EnableCaching
@SpringBootApplication
public class AvatarApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvatarApplication.class, args);
	}

}
