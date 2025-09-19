package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.security.PasswordRecoveryTokenCookieFactory;
import com.epam.rd.autocode.spring.project.security.TokenCookieJweStringSerializer;
import com.epam.rd.autocode.spring.project.service.UserService;
import com.epam.rd.autocode.spring.project.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordController {
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordRecoveryTokenCookieFactory passwordRecoveryTokenCookieFactory;
    private final TokenCookieJweStringSerializer tokenCookieJweStringSerializer;

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordPost(@RequestParam String email) {
        if (userService.existsByEmail(email)) {
            var token = passwordRecoveryTokenCookieFactory.apply(email);
            emailService.sendPasswordReset(email, tokenCookieJweStringSerializer.apply(token));
        }

        return "redirect:/login?status=info_check_email";
    }
}
