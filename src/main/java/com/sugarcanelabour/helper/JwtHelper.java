package com.sugarcanelabour.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.exception.AuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtHelper {

	// private String SECRET_KEY = "my-very-secret-and-long-key-which-is-32-bytes";
	 
	 @Value("${jwt.privateKey.path}")
	    private String privateKeyPath;

	    @Value("${jwt.publicKey.path}")
	    private String publicKeyPath;

	    // Private key method
	    private RSAPrivateKey getPrivateKey() {
	        try {
	            StringBuilder pemContent = new StringBuilder();
	            try (BufferedReader reader = new BufferedReader(new FileReader(privateKeyPath))) {
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    if (!line.startsWith("-----")) {
	                        pemContent.append(line);
	                    }
	                }
	            }

	            byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(pemContent.toString());
	            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	        } catch (Exception e) {
	            throw new AuthenticationException("Error loading private key: " + e.getMessage());
	        }
	    }

	    // Public key method
	    private RSAPublicKey getPublicKey() {
	        try {
	            File publicKeyFile = new File(publicKeyPath);
	            if (!publicKeyFile.exists()) {
	                throw new AuthenticationException("Public key file not found at path: " + publicKeyPath);
	            }

	            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
	            String keyContent = new String(publicKeyBytes).replaceAll("\\n", "").replaceAll("-----\\w+ PUBLIC KEY-----", "");
	            byte[] decodedKey = java.util.Base64.getDecoder().decode(keyContent);

	            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	        } catch (Exception e) {
	            throw new AuthenticationException("Error loading public key: " + e.getMessage());
	        }
	    }

	    // Extract the username from the JWT
	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }

	    // Extract the user ID from the JWT
	    public Long extractUserId(String token) {
	        return extractClaim(token, claims -> Long.valueOf(claims.get("userId").toString()));
	    }

	    // Extract the expiration date from the JWT
	    public Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }

	    // Extract claims from the JWT
	    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimsResolver.apply(claims);
	    }

	    // Extract all claims from the JWT
	    public Claims extractAllClaims(String token) {
	        try {
	            return Jwts.parser().setSigningKey(getPublicKey()) // Use public key for verification
	                    .build().parseClaimsJws(token).getBody();
	        } catch (Exception e) {
	            throw new AuthenticationException("Invalid JWT token: " + e.getMessage());
	        }
	    }

	    // Check if the token has expired
	    private Boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }

	    // Generate the JWT token
	    public String generateToken(CommonLogin userDetails, String userStatus) {
	        Map<String, Object> claims = new HashMap<>();
	        if ("IN-ACTIVE".equalsIgnoreCase(userStatus)) {
	            throw new AuthenticationException("User account is IN-ACTIVE. Token cannot be generated.");
	        }
	        claims.put("userId", userDetails.getUserId());
	        claims.put("role", userDetails.getRole().getRoleName());  // Add role to claims

	        return createToken(claims, userDetails.getEmail());
	    }

	    // Create JWT token using RS256 and private key for signing
	    private String createToken(Map<String, Object> claims, String subject) {
	        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 15)) // 15 days expiration
	                .signWith(getPrivateKey(), SignatureAlgorithm.RS256).compact();
	    }

	    // Validate the JWT token
	    public Boolean validateToken(String token, UserDetails userDetails) {
	        final String username = extractUsername(token);
	        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }


}
