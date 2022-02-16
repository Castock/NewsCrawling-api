package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackageClasses = {"com.example.demo.persistence"})
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class JuSigApplication {

	public static void main(String[] args) {
		SpringApplication.run(JuSigApplication.class, args);
	}
}