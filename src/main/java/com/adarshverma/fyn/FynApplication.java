package com.adarshverma.fyn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FynApplication {

	public static void main(String[] args) {
		SpringApplication.run(FynApplication.class, args);

	}


}
