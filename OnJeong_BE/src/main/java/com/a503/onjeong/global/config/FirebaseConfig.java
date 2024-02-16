package com.a503.onjeong.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;


@Configuration
public class FirebaseConfig {
    @Value("${firebase.config}")
    private String firebaseConfig;
    @Value("${firebase.project.id}")
    private String firebaseProjectId;

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = new FileInputStream(firebaseConfig);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(firebaseProjectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
