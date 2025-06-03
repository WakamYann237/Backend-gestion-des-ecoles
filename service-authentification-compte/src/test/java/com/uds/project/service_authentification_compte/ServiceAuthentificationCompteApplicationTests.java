package com.uds.project.service_authentification_compte;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class ServiceAuthentificationCompteApplicationTests {

	@BeforeAll
    public static void setup() {
        Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("APP_SECRET_KEY", dotenv.get("APP_SECRET_KEY"));
    }

    @Test
    void contextLoads() {
    }

}
