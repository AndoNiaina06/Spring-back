package com.starter.starter.controller;

import com.starter.starter.model.User;
import com.starter.starter.repository.UserRepo;
import com.starter.starter.services.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins="http://localhost:5173")

@RequestMapping("/api/manage")
public class ManageUserController {
    @Autowired
    private UserServiceImp userService;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/list-users")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok().body(users);
    }
    @PostMapping("archive-user/{id}")
    public ResponseEntity<?> archiveUser(@PathVariable Long id) {
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
    @PostMapping("active-user/{id}")
    public ResponseEntity<?> activeUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(true);
            userRepo.save(user);
            return ResponseEntity.ok(Collections.singletonMap("message", "User actived successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
    }


}
