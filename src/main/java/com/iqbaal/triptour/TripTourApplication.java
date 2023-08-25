package com.iqbaal.triptour;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.iqbaal.triptour.service.utils.UploadFile;

import jakarta.annotation.Resource;

@SpringBootApplication
public class TripTourApplication implements CommandLineRunner {

	@Resource
	UploadFile uploadFile;

	public static void main(String[] args) {
		SpringApplication.run(TripTourApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		uploadFile.initUpload();
	}
}
