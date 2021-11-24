package com.renatoav.security.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renatoav.security.entity.Usuario;
import com.renatoav.security.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final String SECRET = "secret";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());
    private final Integer ACCESS_TOKEN_VALIDITY = 10 * 60 * 1000;
    private final Integer REFRESH_TOKEN_VALIDITY = 30 * 60 * 1000;
    private final UsuarioService usuarioService;

    public void generateToken(HttpServletRequest request, HttpServletResponse response, Usuario user) throws IOException {
        String accessToken = createToken(user, request);
        String refreshToken = createRefreshToken(user, request);
        addToResponse(accessToken, refreshToken, response);
    }

    public String createToken(Usuario user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public String createRefreshToken(Usuario user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    public void addToResponse(String accessToken, String refreshToken, HttpServletResponse response) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    public void refresh(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws IOException {
        DecodedJWT decodedJWT = decode(refreshToken);
        Usuario usuario = usuarioService.obterUsuarioPorUsername(decodedJWT.getSubject());
        generateToken(request, response, usuario);
    }

    public DecodedJWT decode(String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

}
