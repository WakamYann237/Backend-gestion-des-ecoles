package com.uds.project.service_authentification_compte;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceAuthentificationCompteApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();

        System.out.println("Loaded DB_USERNAME: " + dotenv.get("DB_USERNAME"));

        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("APP_SECRET_KEY", dotenv.get("APP_SECRET_KEY"));

		SpringApplication.run(ServiceAuthentificationCompteApplication.class, args);
	}

}
