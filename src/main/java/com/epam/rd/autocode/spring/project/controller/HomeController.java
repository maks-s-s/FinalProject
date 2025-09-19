package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email).orElseThrow(NotFoundException::new);
        model.addAttribute("username", user.getUsername());
        return "home";
    }
    }
