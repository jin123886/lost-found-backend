package com.example.lost_found_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.example.lost_found_backend.mapper")
@EnableTransactionManagement
public class LostFoundBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LostFoundBackendApplication.class, args);
	}

}
