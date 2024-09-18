package org.truckwebsite.demo.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.truckwebsite.demo.DBHelper.FirebaseConnection;
import org.truckwebsite.demo.model.News;
import org.truckwebsite.demo.model.Trip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class NewsService {

    Firestore db = FirebaseConnection.getDb();

    public NewsService() throws IOException {
    }


    public String createNews(News news) throws ExecutionException, InterruptedException {
        // Yeni bir belge referansı oluştur
        if (news.getId()=="-1") {
            DocumentReference docRef = db.collection("news").document();
            ApiFuture<WriteResult> result = docRef.set(news);
            // Belgenin ID'sini al ve döndür
            return docRef.getId();
        }else {
            // Belge referansı oluştur
            DocumentReference docRef = db.collection("news").document(news.getId());
            // Belgeyi güncelle
            ApiFuture<WriteResult> result = docRef.set(news);
            return docRef.getId();
        }
    }

    public News getNews(String titleNews) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("news").document(titleNews);
        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = docRef.get();
        com.google.cloud.firestore.DocumentSnapshot document = future.get();
        News news = null;
        if (document.exists()) {
            // convert document to POJO
            news = document.toObject(News.class);
            return news;
        } else {
            System.out.println("No such document!");
            return null;
        }
    }

    public List<News> getAllNews() throws ExecutionException, InterruptedException, IOException {
        CollectionReference newsCollection = db.collection("news");

        // Asenkron olarak tüm news belgelerini getir
        ApiFuture<QuerySnapshot> querySnapshot = newsCollection.get();

        List<News> newsList = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            News news = document.toObject(News.class);
            news.setId(document.getId());
            newsList.add(news);
        }
        return newsList;
    }

    public News getNewsById(String id) throws ExecutionException, InterruptedException, IOException {
        DocumentReference docRef = db.collection("news").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        News news = null;
        if (document.exists()) {
            news = document.toObject(News.class);
            news.setId(document.getId());
        }
        return news;
    }

    public void deleteNews(String newsId) throws ExecutionException, InterruptedException {
        // Belge referansı oluştur
        DocumentReference docRef = db.collection("news").document(newsId);
        // Belgeyi sil
        ApiFuture<WriteResult> result = docRef.delete();
    }




}
