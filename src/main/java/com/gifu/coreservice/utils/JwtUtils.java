package com.gifu.coreservice.utils;

import com.gifu.coreservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class JwtUtils {
    private static PrivateKey getPrivateKey() throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        FileUtils util = new FileUtils();
//        String key = Files.readString(util.getFileFromResource("private_key.pem").toPath(), Charset.defaultCharset());
        try(InputStream is = JwtUtils.class.getResourceAsStream("/private_key.pem")){
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            key = key.replace("-----BEGIN PRIVATE KEY-----", "");
            key = key.replace("-----END PRIVATE KEY-----", "");
            key = key.replaceAll("[\\t\\n\\r]+","");

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(keySpec);
            return privateKey;
        }


    }

    private static PublicKey getPublicKey() throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        FileUtils util = new FileUtils();
//        String key = Files.readString(util.getFileFromResource("public_key.pem").toPath(), Charset.defaultCharset());
        try(InputStream is = JwtUtils.class.getResourceAsStream("/public_key.pem")){
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            key = key.replace("-----BEGIN PUBLIC KEY-----", "");
            key = key.replace("-----END PUBLIC KEY-----", "");
            key = key.replaceAll("[\\t\\n\\r]+","");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        }

    }

    public static String createJwtSignedHMAC(User user) throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, IOException {

        PrivateKey privateKey = getPrivateKey();

        Instant now = Instant.now();

        return Jwts.builder()
                .claim("name", user.getName())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("roleId", user.getRoleId())
                .claim("permissions", user.getAuthorities())
                .setSubject(user.getName())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(30, ChronoUnit.MINUTES)))
                .signWith(privateKey)
                .compact();
    }

    public static Jws<Claims> parseJwt(String jwtString) throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, IOException {

        PublicKey publicKey = getPublicKey();

        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(jwtString);
    }
}
