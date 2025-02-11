package com.starter.starter.controller;


import com.starter.starter.services.UserServiceImp;
import com.starter.starter.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.starter.starter.model.User;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@CrossOrigin(origins="http://localhost:5173")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserServiceImp userService;



    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        User saveUser = userService.saveUser(user);
        return ResponseEntity.ok(saveUser);
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
    //generer un token pour l'utilisateur
    private String generateToken(User user) {
        return UUID.randomUUID().toString();
    }


}
