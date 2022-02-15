package com.prasad.nithin.playtech.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.prasad.nithin.playtech.server.persistance.PlayerPersistance;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

	@Autowired
	PlayerPersistance persistance;
	
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
		
	}

}
