package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.model.enums.UserRole;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    User registerUser(String username, String email, String rawPassword, UserRole role);
    void changePassword(String email, String newPassword);
}
