package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.Permission;
import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.UserDto;
import com.gifu.coreservice.model.request.SignupRequest;
import com.gifu.coreservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean signUp(SignupRequest request) throws InvalidRequestException {
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new InvalidRequestException("Please to confirm password correctly", null);
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsEnabled(true);
        userRepository.save(user);
        return true;
    }

    public List<UserDto> getUserRefs(){
        List<User> users = userRepository.findAll();
        return users.stream().map(it->UserDto.builder()
                .id(it.getId())
                .name(it.getName())
                .build()).collect(Collectors.toList());
    }

}
