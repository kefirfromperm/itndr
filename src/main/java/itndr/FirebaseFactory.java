package itndr;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

import java.io.IOException;

@Factory
public class FirebaseFactory {
    private String projectId;

    @Value("gcp.project-id")
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Bean
    public Firestore firestore() throws IOException {
        FirestoreOptions firestoreOptions =
                FirestoreOptions.getDefaultInstance().toBuilder()
                        .setProjectId(projectId)
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
        return firestoreOptions.getService();
    }
}