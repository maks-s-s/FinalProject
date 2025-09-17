package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.model.enums.UserRole;

public interface UserService {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    public void registerUser(String username, String email, String rawPassword, String phoneNumber, UserRole role);
}
