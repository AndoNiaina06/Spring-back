package com.starter.starter.services;

import com.starter.starter.model.User;
import com.starter.starter.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    private UserRepo userRepo;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User login(User user) {
        String email = user.getEmail();
        User existingUser = userRepo.findByEmail(email);

        if (existingUser == null) {
            return null;
        }

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return new User();
        }
        return existingUser;
    }

    @Override
    public User updateUser(Long id, User userRequest) {

        Optional<User> optionalUser = userRepo.findById(id);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setFname(userRequest.getFname());
            existingUser.setLname(userRequest.getLname());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setType(existingUser.getType());
            existingUser.setPhotoUrl(userRequest.getPhotoUrl());

            return userRepo.save(existingUser);
        }

        return null;
    }

    @Transactional
    public boolean updatePasswordByEmail(String email, String newPassword) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            return true;
        }
        return false;
    }
}
