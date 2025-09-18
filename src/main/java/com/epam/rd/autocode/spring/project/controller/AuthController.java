package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.model.enums.UserRole;
import com.epam.rd.autocode.spring.project.security.PasswordRecoveryTokenCookieFactory;
import com.epam.rd.autocode.spring.project.security.Token;
import com.epam.rd.autocode.spring.project.security.TokenCookieJweStringDeserializer;
import com.epam.rd.autocode.spring.project.security.TokenCookieJweStringSerializer;
import com.epam.rd.autocode.spring.project.service.UserService;
import com.epam.rd.autocode.spring.project.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.function.Function;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private Function<String, Token> passwordRecoveryTokenCookieFactory = new PasswordRecoveryTokenCookieFactory();
    private final TokenCookieJweStringSerializer tokenCookieJweStringSerializer;
    private final TokenCookieJweStringDeserializer tokenCookieJweStringDeserializer;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {model.addAttribute("loginError", true);}
        if (logout != null) {model.addAttribute("logoutMessage", true);}

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", true);
            return "register";
        }

        log.info("Регистрация пользователя: {}", user.getEmail());
        userService.registerUser(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                UserRole.CUSTOMER
        );

        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordPost(@RequestParam String email,
            Model model) {
        if (userService.existsByEmail(email)) {
            var token = passwordRecoveryTokenCookieFactory.apply(email);
            emailService.sendPasswordReset(email, tokenCookieJweStringSerializer.apply(token));
        }
        else {
            model.addAttribute("UserWithThisEmailNotFound", true);
            return "forgot-password";
        }
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordPost(@RequestParam(name = "token") String tokenName,
                                    @RequestParam String newPassword,
                                    RedirectAttributes redirectAttributes) {
        log.info("Reset password called with token: {}", tokenName);
        if (!tokenName.isBlank()) {
            log.warn("Token is blank");
            Token token;
            try {
                token = tokenCookieJweStringDeserializer.apply(tokenName);
                log.info("Token deserialized: {}", token);
            } catch (Exception e) {log.error("Invalid token", e);return "redirect:/login";}
            if (token.expiresAt().isBefore(Instant.now())) {
                log.warn("Token expired at {}", token.expiresAt());
                return "redirect:/login";
            }
            if (token.expiresAt().isAfter(Instant.now())) {
                log.info("Password reset for email: " + token.subject());
                userService.changePassword(token.subject(), newPassword);

                redirectAttributes.addFlashAttribute("successfulPasswordReset", true);
                return "redirect:/login";
            }
        }
            return "redirect:/login";
    }
}
