package com.fishing.demo;

import com.fishing.demo.model.User;
import com.fishing.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FishingBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FishingBookingApplication.class, args);
	}

	@Bean
	public CommandLineRunner testRepository(UserRepository userRepository) {
		return args -> {
			String testEmail = "testuser@example.com";
			// Verificăm dacă există deja un user cu acest email
			if (userRepository.findByEmail(testEmail).isEmpty()) {
				User user = new User();
				user.setUsername("testuser");
				user.setEmail(testEmail);
				user.setPassword("parola123");
				userRepository.save(user);
				System.out.println("User creat: " + user);
			} else {
				System.out.println("Userul cu emailul " + testEmail + " există deja.");
			}
			// Afișăm toți userii din baza de date
			userRepository.findAll().forEach(System.out::println);
		};
	}
}
