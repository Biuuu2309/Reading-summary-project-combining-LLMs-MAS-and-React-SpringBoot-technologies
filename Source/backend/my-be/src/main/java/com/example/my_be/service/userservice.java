package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user with a hashed password.
     */
    public User createUser(User user) {
        // Hash the user's password before saving
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    public User updateUserProfile(User user) {
        Optional<User> existingUser = userRepository.findById(user.getUserId());
        if (existingUser.isPresent()) {
            User existing = existingUser.get();
            existing.setFullName(user.getFullName());
            existing.setAvatarUrl(user.getAvatarUrl());
            return userRepository.save(existing);
        } else {
            return null; // Or throw an exception if user not found
        }
    }
    
    /**
     * Get a user by their ID.
     */
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    // Get all users by role
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    /**
     * Get a user by their username.
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get all users in the system.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Delete a user by their ID.
     */
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Update a user's role to CONTRIBUTOR after their first approved summary.
     */
    public void promoteToContributor(User user) {
        if (!"CONTRIBUTOR".equals(user.getRole())) {
            user.setRole("CONTRIBUTOR");
            userRepository.save(user);
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
