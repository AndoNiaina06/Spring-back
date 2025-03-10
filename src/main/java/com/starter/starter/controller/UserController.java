package com.starter.starter.controller;


import com.starter.starter.repository.UserRepo;
import com.starter.starter.services.EmailService;
import com.starter.starter.services.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import com.starter.starter.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@RestController
@CrossOrigin(origins="http://localhost:5173")

@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserServiceImp userService;
    @Autowired
    private UserRepo userRepo;
    private static final String UPLOAD_DIR = "C:\\Users\\Ando Niaina\\Documents\\Front-end\\public\\image";
    @Autowired
    private EmailService emailService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    public ResponseEntity<Map<String, Object>> login(@RequestBody User requestUser) {
        User user = userService.login(requestUser);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Email not found"));
        }

        if (user.getId() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid password"));
        }

        if (!user.isStatus()) {
            return ResponseEntity.status(403).body(Map.of("message", "Your account is archived, contact support"));
        }

        String token = generateToken(user);

        return ResponseEntity.ok(Map.of(
                "user", user,
                "token", token
        ));
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

        if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please check your old Password");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(existingUser);

        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }
    @PatchMapping("/reset-password/{email}")
    public ResponseEntity<?> resetPassword(@PathVariable String email, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");

        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"message\": \"Password cannot be empty\"}");
        }

        boolean updated = userService.updatePasswordByEmail(email, newPassword);

        if (updated) {
            return ResponseEntity.ok().body("{\"message\": \"Password updated successfully\"}");
        } else {
            return ResponseEntity.status(404).body("{\"message\": \"User not found\"}");
        }
    }


    @PostMapping("/sendCode")
    public ResponseEntity<Map<String, String>> sendResetCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        System.out.println("Email reçu: " + email);
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Utilisateur non trouvé"));
        }

        String verificationCode;
        try {
            verificationCode = emailService.sendVerificationCode(email);
            return ResponseEntity.ok(Map.of("code", verificationCode));
        } catch (Exception e) {
            e.printStackTrace(); // Log l'erreur complète
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de l'envoi du code"));
        }

    }
    @PutMapping("archive/{id}")
    public ResponseEntity <?> archiveUser(@PathVariable long id){
        Optional<User> optionalUser = userRepo.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(false);
            userRepo.save(user);
            return ResponseEntity.ok(Collections.singletonMap("message", "User archived successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepo.findById(id);

        if (optionalUser.isPresent()) {
            userRepo.deleteById(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
    }
    @GetMapping("/user-types")
    public ResponseEntity<Map<String, Long>> getUserTypes() {
        Long totalAdmins = userRepo.countByType("admin");
        Long totalUsers = userRepo.countByType("user");

        Map<String, Long> stats = new HashMap<>();
        stats.put("admins", totalAdmins);
        stats.put("users", totalUsers);

        return ResponseEntity.ok(stats);
    }

    // 🔹 2️⃣ Statistiques pour les utilisateurs par mois
    @GetMapping("/users-per-month")
    public ResponseEntity<Map<String, Long>> getUsersPerMonth() {
        List<Object[]> results = userRepo.countUsersByMonth();
        Map<String, Long> stats = new LinkedHashMap<>();

        // Liste des noms des mois en français
        String[] months = {
                "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };

        // Initialiser les mois à zéro pour éviter les valeurs nulles
        for (String month : months) {
            stats.put(month, 0L);
        }

        // Ajouter les valeurs récupérées de la base
        for (Object[] row : results) {
            String month = row[0].toString();
            Long count = (Long) row[1];
            int monthIndex = Integer.parseInt(month) - 1;  // Mois commence à 1
            stats.put(months[monthIndex], count);
        }

        return ResponseEntity.ok(stats);
    }

}
