package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    List<User> findByRoleNot(String role);
    Optional<User> findByEmail(String email);

    List<User> findByRoleAndUsernameNot(String role, String username);
    //getId
    
}
