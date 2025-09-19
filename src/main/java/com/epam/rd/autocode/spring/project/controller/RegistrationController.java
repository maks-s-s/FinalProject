package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.model.enums.UserRole;
import com.epam.rd.autocode.spring.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {
    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", true);
            return "register";
        }

        userService.registerUser(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                UserRole.CUSTOMER
        );

        return "redirect:/login?status=info_successful_registration";
    }
}
