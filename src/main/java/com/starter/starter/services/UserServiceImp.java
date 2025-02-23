package com.starter.starter.services;

import com.starter.starter.model.User;
import com.starter.starter.repository.UserRepo;
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
            existingUser.setType(userRequest.getType());
            existingUser.setPhotoUrl(userRequest.getPhotoUrl());

            return userRepo.save(existingUser); // Sauvegarde en base de données
        }

        return null;
    }

}
