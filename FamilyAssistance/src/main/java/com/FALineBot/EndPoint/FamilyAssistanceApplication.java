package com.FALineBot.EndPoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FamilyAssistanceApplication {

	public static void main(String[] args) {
		try {
		SpringApplication.run(FamilyAssistanceApplication.class, args);
		}catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
