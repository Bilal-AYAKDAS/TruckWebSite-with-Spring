package org.truckwebsite.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserService {

    private static final String API_KEY = "AIzaSyBgLQfY6yDWFXgZQyuyEMFPPkVF_EviOVI"; // Firebase projenizin API anahtarını buraya ekleyin
    private static final String LOGIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    private String idToken;
    private FirebaseToken decodedToken;

    /**
     * Yeni bir kullanıcı oluşturur.
     */
    public UserRecord createUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);
        return FirebaseAuth.getInstance().createUser(request);
    }

    /**
     * Kullanıcıyı giriş yapar ve Firebase kimlik doğrulama tokenını döner.
     */
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

            // ID Token'ı al
            idToken = (String) responseMap.get("idToken");

            // ID Token'ı doğrula ve decodedToken'ı sakla
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // Kullanıcı kimliğini (UID) döndür
            return decodedToken.getUid();
        } else {
            throw new RuntimeException("Giriş başarısız: " + response.getBody());
        }
    }

    /**
     * Email ile kullanıcıyı bulur.
     */
    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUserByEmail(email);
    }

    /**
     * Giriş yapmış olan kullanıcıyı döner.
     */
    public String getCurrentUser() throws FirebaseAuthException {
        if (decodedToken == null) {
            throw new RuntimeException("Kullanıcı oturum açmamış.");
        }

        String uid = decodedToken.getUid();
        return uid;
    }
}
