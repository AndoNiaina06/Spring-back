package com.starter.starter.services;

import com.starter.starter.model.User;
import com.starter.starter.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Override
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public User login(User user) {
        String email = user.getEmail();

        return userRepo.findByEmail(email);
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
            user.setPassword(newPassword);
            userRepo.save(user);
            return true;
        }
        return false;
    }
}
