package com.renatoav.security.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.renatoav.security.controller.dto.LoginRequest;
import com.renatoav.security.entity.Usuario;
import com.renatoav.security.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginRequest loginRequest) throws IOException {
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        try {
            authentication = authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            throw new BadCredentialsException("Login invalido");
        }

        authenticationService.generateToken(request, response, (Usuario) authentication.getPrincipal());
    }

    @GetMapping("/token/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = request.getHeader(AUTHORIZATION);

        try {
            authenticationService.refresh(request, response, refreshToken);
        } catch (TokenExpiredException exp) {
            response.setStatus(UNAUTHORIZED.value());
        }
    }

    @GetMapping("/home")
    public String home() throws IOException {
        return "Bem vindo";
    }


}
