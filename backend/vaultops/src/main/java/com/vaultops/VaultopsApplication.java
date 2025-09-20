package com.vaultops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VaultopsApplication {

	public static void main(String[] args) {
        System.out.println("Welcome to VaultOps!");
		SpringApplication.run(VaultopsApplication.class, args);
	}

}
