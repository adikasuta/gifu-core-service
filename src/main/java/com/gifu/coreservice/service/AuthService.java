package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.repository.UserRepository;
import com.gifu.coreservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User findByEmailAndPassword(String email, String password) {
        Authentication authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(email, password)
                );
        return (User) authenticate.getPrincipal();
    }

    public String login(String email, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, IOException {
        User user = findByEmailAndPassword(email, password);
        return JwtUtils.createJwtSignedHMAC(user);
    }

    public User changePassword(String email, String oldPassword, String newPassword) {
        User user = findByEmailAndPassword(email, oldPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
