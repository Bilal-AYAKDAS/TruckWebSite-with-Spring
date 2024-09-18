package org.truckwebsite.demo.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.truckwebsite.demo.model.User;
import org.truckwebsite.demo.service.UserService;
import org.truckwebsite.demo.DBHelper.FirebaseConnection;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private FirebaseConnection userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            UserRecord userRecord = userService.createUser(user);
            return ResponseEntity.ok(userRecord);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        try {
            UserRecord userRecord = userService.getUserByEmail(email);
            return ResponseEntity.ok(userRecord);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/getUserByID/{userId}")
    public ResponseEntity<?> getUserByID(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            String authToken = userService.loginUser(user.getEmail(), user.getPassword());
            return ResponseEntity.ok(authToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

}
