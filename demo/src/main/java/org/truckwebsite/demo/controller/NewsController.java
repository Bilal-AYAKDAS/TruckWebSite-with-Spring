package org.truckwebsite.demo.controller;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.truckwebsite.demo.DBHelper.FirebaseConnection;
import org.truckwebsite.demo.model.News;
import org.truckwebsite.demo.service.NewsService;
import org.truckwebsite.demo.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @PostMapping("/createNews")
    public ResponseEntity<String> createNews(@RequestPart("news") News news,  @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            String documentId = newsService.createNews(news);

            if (image != null) {

                // Resim dosyasının bytes'a çevrilmesi
                byte[] bytes = image.getBytes();

                // Proje kök dizinini al
                Path currentRelativePath = Paths.get("");
                String projectRoot = currentRelativePath.toAbsolutePath().toString();
                System.out.println("Current relative path is: " + projectRoot);

                // Dosya yolu belirlenir ve dosya kaydedilir
                Path path = Paths.get(projectRoot, "demo","src", "main", "resources", "static", "Pictures", documentId + ".jpg");
                System.out.println("Path: " + path.toAbsolutePath().toString());
                Files.createDirectories(path.getParent()); // Klasörlerin var olduğundan emin olun
                Files.write(path, bytes);
            }

            return ResponseEntity.ok("Başarıyla Kayıt Edildi");
        } catch (ExecutionException | InterruptedException | IOException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/getAllNews")
    public ResponseEntity<List<News>> getAllNews() {
        try {
            List<News> newsList = newsService.getAllNews();
            return ResponseEntity.ok(newsList);
        } catch (ExecutionException | InterruptedException | IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @DeleteMapping("/deleteNews")
    public ResponseEntity<String> deleteNews(@RequestParam String id) {
        try {
            newsService.deleteNews(id);

            return ResponseEntity.ok("Başarıyla Silindi");
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }




}
