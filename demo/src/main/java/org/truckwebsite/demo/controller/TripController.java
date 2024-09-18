package org.truckwebsite.demo.controller;

import com.google.firebase.auth.FirebaseAuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import org.truckwebsite.demo.model.News;
import org.truckwebsite.demo.model.Trip;
import org.truckwebsite.demo.service.NewsService;
import org.truckwebsite.demo.service.TripService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/createTrip")
    public ResponseEntity<String> createTrip(@RequestPart("trip") Trip trip, @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            String documentId;
            if (trip.getId() == null || trip.getId().isEmpty() || trip.getId().equals("null") || trip.getId().equals("undefined") || trip.getId().equals("-1")) {
                documentId = tripService.createTrip(trip);

            }else {
                tripService.updateTrip(trip);
                documentId = trip.getId();
            }

            if (image == null || image.isEmpty()) {
                return ResponseEntity.ok(documentId);
            }
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

            return ResponseEntity.ok(documentId);
        } catch (ExecutionException | InterruptedException | IOException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/getAllTrip")
    public ResponseEntity<List<Trip>>  getAllTrip() {
        try {
            List<Trip> tripList = tripService.getAllTrip();
            return ResponseEntity.ok(tripList);
        } catch (ExecutionException | InterruptedException | IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/getTripByCities")
    public ResponseEntity<List<Trip>> getTripsByCities(@RequestBody Map<String, String> cities) {
        try {
            String nereden = cities.get("nereden");
            String nereye = cities.get("nereye");
            List<Trip> tripList = tripService.getTripByCities(nereden, nereye);
            return ResponseEntity.ok(tripList);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/getTripById")
    public ResponseEntity<Trip> getTripById(@RequestParam String id) {
        try {
            Trip trip = tripService.getTripById(id);
            return ResponseEntity.ok(trip);
        } catch (ExecutionException | InterruptedException | IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getTripByUserId")
    public ResponseEntity<List<Trip>> getTripByUserId(@RequestParam String userId) {
        try {
            List<Trip> tripList = tripService.getTripsByUserId(userId);
            return ResponseEntity.ok(tripList);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/deleteTrip")
    public ResponseEntity<String> deleteTrip(@RequestParam String id) {
        try {
            tripService.deleteTrip(id);
            return ResponseEntity.ok("Başarıyla Silindi");
        } catch (ExecutionException | InterruptedException | IOException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }





}
