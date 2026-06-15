package com.project.uber.uberApp;

import com.project.uber.uberApp.services.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest//@SpringBootTest starts the complete Spring Boot application in a test environment so that you can test how multiple components
//work together.
class UberAppApplicationTests {

	@Autowired
	private EmailSenderService emailSenderService;

	@Test
	void contextLoads() {
		emailSenderService.sendEmail("mahimishra3004@gmail.com",
				"INTRO",
				"Hi Mahi Mishra how are you");

	}

}
