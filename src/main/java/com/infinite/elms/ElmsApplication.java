package com.infinite.elms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ElmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElmsApplication.class, args);
	}

}
