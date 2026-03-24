package com.example.PopcornCinema.service;

import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // FIND
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    // REGISTER
    public void register(User user){

        // 🔥 SET DEFAULT
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");

        userRepository.save(user);
    }

    // LOGIN (optional)
    public User login(String email, String password){

        User user = userRepository.findByEmail(email);

        if(user != null && user.getPasswordHash().equals(password)){
            return user;
        }

        return null;
    }
}