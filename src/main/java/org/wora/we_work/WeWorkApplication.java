package org.wora.we_work;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeWorkApplication.class, args);
		System.out.println("we work");
	}

}
