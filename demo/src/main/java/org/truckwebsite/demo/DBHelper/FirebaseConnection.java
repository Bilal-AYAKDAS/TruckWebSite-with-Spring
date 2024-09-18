package org.truckwebsite.demo.DBHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.truckwebsite.demo.model.Trip;
import org.truckwebsite.demo.model.User;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseConnection {
    private static Firestore db;
    private static FirebaseOptions options;
    private String idToken;
    private FirebaseToken decodedToken;

    private static final String API_KEY = ""; // Firebase projenizin API anahtarını buraya ekleyin
    private static final String LOGIN_URL = "" + API_KEY;

    @PostConstruct
    public void connect() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

        if (serviceAccount == null) {
            throw new IOException("serviceAccountKey.json dosyası bulunamadı!");
        }

        options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        db = FirestoreClient.getFirestore();
    }

    public static Firestore getDb() throws IOException {
        if (db == null) {
            new FirebaseConnection().connect();
        }
        return db;
    }

    public UserRecord createUser(User user) throws FirebaseAuthException {
        // Firebase Authentication ile kullanıcıyı oluştur
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        // Kullanıcı UID'sini al
        String uid = userRecord.getUid();

        // Firestore bağlantısını al
        Firestore db = FirestoreClient.getFirestore();

        // Kullanıcı bilgilerini Firestore'a kaydet
        DocumentReference userDocRef = db.collection("users").document(uid);
        userDocRef.set(user);

        return userRecord;
    }

    public String loginUser(String email, String password) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String jsonInputString = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"returnSecureToken\": true}", email, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonInputString, headers);

        ResponseEntity<String> response = restTemplate.exchange(LOGIN_URL, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);

            idToken = (String) responseMap.get("idToken");
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            return decodedToken.getUid();
        } else {
            throw new RuntimeException("Giriş başarısız: " + response.getBody());
        }
    }

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUserByEmail(email);
    }

    public String getCurrentUser() {
        if (decodedToken == null) {
            throw new RuntimeException("Kullanıcı oturum açmamış.");
        }
        return decodedToken.getUid();
    }

    public User getUserById(String uid) throws FirebaseAuthException {
        DocumentReference docRef = db.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        User user = null;
        if (document.exists()) {
            user = document.toObject(User.class);
            user.setUid(document.getId());
        }
        return user;
    }
}