package com.starter.starter.controller;


import com.starter.starter.repository.UserRepo;
import com.starter.starter.services.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.starter.starter.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;


@RestController
@CrossOrigin(origins="http://localhost:5173")

@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserServiceImp userService;
    @Autowired
    private UserRepo userRepo;
    private static final String UPLOAD_DIR = "C:\\Users\\Ando Niaina\\Documents\\Front-end\\public\\image";


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        User saveUser = userService.saveUser(user);
        return ResponseEntity.ok(saveUser);
    }
    @GetMapping("/user")
    public ResponseEntity<User> getUserData(@RequestParam String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<Object, Object>> login(@RequestBody User requestUser){
        User user =userService.login(requestUser);

        if(user != null){
            String token = generateToken(user);
            token =  token + token;

            return ResponseEntity.ok(Map.of(
                    "user", user,
                    "token", token
            ));
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    private String generateToken(User user) {
        return UUID.randomUUID().toString();
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userRequest) {
        User updatedUser = userService.updateUser(id, userRequest);

        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }

        return ResponseEntity.notFound().build();
    }
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        try {
            User user = userRepo.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Utilisateur non trouvé"));
            }

            // Générer un nom de fichier unique
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // Sauvegarde du fichier
            Files.write(filePath, file.getBytes());
            // Mettre à jour le champ photoUrl de l'utilisateur
            String photoUrl = "http://localhost:8080/images/" + fileName;
            user.setPhotoUrl(photoUrl);
            userRepo.save(user);

            return ResponseEntity.ok(Map.of("photoUrl", photoUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de l'upload"));
        }
    }

    @PatchMapping("/update-password/{id}")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        User existingUser = userRepo.findById(id).orElse(null);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("password");

        if (oldPassword == null || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Les mots de passe ne peuvent pas être vides");
        }

        // Vérifier si l'ancien mot de passe est correct
        if (!existingUser.getPassword().equals(oldPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ancien mot de passe incorrect");
        }

        // Mettre à jour le mot de passe
        existingUser.setPassword(newPassword);
        userRepo.save(existingUser);

        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }



}
