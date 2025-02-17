package com.springboot.auth.controller;


import com.springboot.auth.service.AuthService;
import com.springboot.auth.utils.IdAndEmailPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/logout")
    public ResponseEntity postLogout(Authentication authentication) {
        IdAndEmailPrincipal idAndEmailPrincipal = (IdAndEmailPrincipal) authentication.getPrincipal();

        String username = idAndEmailPrincipal.getEmail();

        authService.logout(username);

        return new ResponseEntity(HttpStatus.OK);
    }
}
