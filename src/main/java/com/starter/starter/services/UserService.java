package com.starter.starter.services;

import com.starter.starter.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public User saveUser(User user);
    public User login(User user);
}
