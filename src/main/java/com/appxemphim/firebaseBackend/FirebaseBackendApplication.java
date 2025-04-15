package com.appxemphim.firebaseBackend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class FirebaseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirebaseBackendApplication.class, args);
    }

    @PostConstruct
    public void initFirebase() {
        try {
            // Load Firebase credentials file from resources folder
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("appxemphim-c633a-firebase-adminsdk-fbsvc-7c8017ca3d.json");

            if (serviceAccount == null) {
                System.err.println(" Firebase service account file not found in resources folder.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println(" Firebase initialized successfully.");
            } else {
                System.out.println(" Firebase already initialized.");
            }

        } catch (IOException e) {
            System.err.println(" Failed to initialize Firebase.");
            e.printStackTrace();
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
