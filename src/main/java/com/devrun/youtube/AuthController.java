package com.devrun.youtube;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;
	
	
    @PostMapping("/googlelogin")
    public ResponseEntity<String> googleLogin(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        GoogleIdTokenVerifier verifier = initializeGoogleIdTokenVerifier();

        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                String accessToken = generateAccessToken(idToken.getPayload());
                return new ResponseEntity<>(accessToken, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private GoogleIdTokenVerifier initializeGoogleIdTokenVerifier() {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    private String generateAccessToken(Map<String, Object> claims) {
        long expirationTimeInMillis = System.currentTimeMillis() + 3600000; // 1 hour
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(expirationTimeInMillis))
                .signWith(SignatureAlgorithm.HS256, clientSecret)
                .compact();
    }
}
