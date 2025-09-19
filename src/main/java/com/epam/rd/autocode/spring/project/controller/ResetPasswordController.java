package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.DeactivatedToken;
import com.epam.rd.autocode.spring.project.repo.DeactivatedTokenRepository;
import com.epam.rd.autocode.spring.project.security.Token;
import com.epam.rd.autocode.spring.project.security.TokenCookieJweStringDeserializer;
import com.epam.rd.autocode.spring.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordController {
    private final UserService userService;
    private final TokenCookieJweStringDeserializer tokenCookieJweStringDeserializer;
    private final DeactivatedTokenRepository deactivatedTokenRepository;

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, Model model) {
        model.addAttribute("token", token);

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordPost(@RequestParam(name = "token") String tokenString,
                                    @RequestParam String newPassword,
                                    @RequestParam String confirmPassword,
                                    Model model) {
        if (tokenString.isBlank()) {return "redirect:/login?status=error_invalid_token";}

        Token token;
        try {token = tokenCookieJweStringDeserializer.apply(tokenString);}
        catch (Exception e) {return "redirect:/login?status=error_invalid_token";}

        if (deactivatedTokenRepository.existsById(token.id())) {return "redirect:/login?status=error_invalid_token";}

        if (token.expiresAt().isBefore(Instant.now())) {return "redirect:/login?status=error_expired_token";}

        if (!token.authorities().contains("PASSWORD_RECOVERY")) {return "redirect:/login?status=error_invalid_token";}

        if (!newPassword.equals(confirmPassword) || newPassword.length() < 8) {
            model.addAttribute("passwordError", "Пароли не совпадают или меньше 8 символов");
            model.addAttribute("token", tokenString);
            return "reset-password";
        }

        userService.changePassword(token.subject(), newPassword);
        deactivatedTokenRepository.save(new DeactivatedToken(token.id(), Instant.now(), token.expiresAt()));
        return "redirect:/login?status=info_successful_password_change";
    }
}
