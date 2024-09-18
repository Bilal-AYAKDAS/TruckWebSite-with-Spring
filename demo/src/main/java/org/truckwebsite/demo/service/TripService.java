package org.truckwebsite.demo.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;
import org.truckwebsite.demo.DBHelper.FirebaseConnection;
import org.truckwebsite.demo.model.News;
import org.truckwebsite.demo.model.Trip;
import org.truckwebsite.demo.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class TripService {

    Firestore db = FirebaseConnection.getDb();
    UserService userService;
    public TripService() throws IOException {
    }

    public String createTrip(Trip trip) throws ExecutionException, InterruptedException {

        DocumentReference docRef = db.collection("users").document(trip.getUserId());
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);
        }

        // Yeni bir belge referansı oluştur ve veriyi asenkron olarak kaydet
        DocumentReference docRef2 = db.collection("trip").document();
        trip.setPhone(user.getPhone());
        ApiFuture<WriteResult> result = docRef2.set(trip);
        // Belgenin ID'sini al ve döndür
        return docRef2.getId();
    }

    public List<Trip> getAllTrip() throws ExecutionException, InterruptedException, IOException {
        CollectionReference newsCollection = db.collection("trip");

        // Asenkron olarak tüm news belgelerini getir
        ApiFuture<QuerySnapshot> querySnapshot = newsCollection.get();

        List<Trip> tripsList = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Trip trip = document.toObject(Trip.class);

            // Belge ID'sini imageURL alanına set et
            trip.setId(document.getId());
            tripsList.add(trip);
        }
        return tripsList;
    }

    public Trip getTripById(String id) throws ExecutionException, InterruptedException, IOException {
        DocumentReference docRef = db.collection("trip").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        Trip trip = null;
        if (document.exists()) {
            trip = document.toObject(Trip.class);
            trip.setId(document.getId());
        }
        return trip;
    }

    public List<Trip> getTripsByUserId(String userId) throws ExecutionException, InterruptedException {
        CollectionReference tripsCollection = db.collection("trip");
        Query query = tripsCollection.whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Trip> tripList = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            // convert document to POJO
            Trip trip = document.toObject(Trip.class);
            trip.setId(document.getId());
            tripList.add(trip);
        }
        return tripList;
    }

    public List<Trip> getTripByCities(String nereden,String nereye) throws ExecutionException, InterruptedException {
        CollectionReference tripsCollection = db.collection("trip");
        Query query = tripsCollection.whereEqualTo("departureCity", nereden).whereEqualTo("destinationCity", nereye);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Trip> tripList = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            // convert document to POJO
            Trip trip = document.toObject(Trip.class);
            trip.setId(document.getId());

            tripList.add(trip);
        }
        return tripList;
    }

    public void updateTrip(Trip trip) throws ExecutionException, InterruptedException {
        // Güncellenecek belgenin referansını al
        DocumentReference docRef = db.collection("trip").document(trip.getId());
        // Asenkron olarak belgeyi güncelle
        ApiFuture<WriteResult> result = docRef.set(trip);
        // İşlem sonucunu bekleyin
        System.out.println("Güncelleme Zamanı: " + result.get().getUpdateTime());
    }

    public void deleteTrip(String id) throws ExecutionException, InterruptedException, IOException {
        DocumentReference docRef = db.collection("trip").document(id);
        ApiFuture<WriteResult> result = docRef.delete();
        System.out.println("Silme Zamanı: " + result.get().getUpdateTime());
    }

}
