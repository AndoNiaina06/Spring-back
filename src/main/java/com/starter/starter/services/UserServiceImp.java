package com.starter.starter.services;

import com.starter.starter.model.User;
import com.starter.starter.repository.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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
}
