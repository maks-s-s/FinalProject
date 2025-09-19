package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.enums.LoginStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "status", required = false) String status,
                            Model model) {
        if (status != null) {
            try {
                LoginStatus loginStatus = LoginStatus.valueOf(status.toUpperCase());
                model.addAttribute("message", loginStatus.getMessage());
                model.addAttribute("messageType", loginStatus.getType());
            } catch (IllegalArgumentException ignored) {}
        }

        return "login";
    }
}
