package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.CsReferral;
import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.UserDto;
import com.gifu.coreservice.model.request.SaveProfileRequest;
import com.gifu.coreservice.model.request.SignupRequest;
import com.gifu.coreservice.repository.CsReferralRepository;
import com.gifu.coreservice.repository.UserRepository;
import com.gifu.coreservice.utils.FileUtils;
import com.gifu.coreservice.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CsReferralRepository csReferralRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${picture.path}")
    private String pictureBasePath;

    public CsReferral generateNewReferralCode(Long userId){
        String token = StringUtils.generateReferralCode();
        long existingCount = csReferralRepository.countByToken(token);
        if(existingCount>0){
            generateNewReferralCode(userId);
        }
        List<CsReferral> legacyCodes = csReferralRepository.findActiveByUserId(userId);
        for(CsReferral it : legacyCodes){
            it.setInactiveDate(ZonedDateTime.now());
            csReferralRepository.save(it);
        }
        CsReferral newCode = new CsReferral();
        newCode.setUserId(userId);
        newCode.setCreatedDate(ZonedDateTime.now());
        newCode.setToken(token);
        return csReferralRepository.save(newCode);
    }

    public Pair<User, String> createUserAccount(SaveProfileRequest request, MultipartFile pictureFile, String createdBy) throws IOException {
        FileUtils fileUtils = new FileUtils();
        User user = new User();
        if(pictureFile!=null){
            String filepath = fileUtils.storeFile(pictureFile, pictureBasePath);
            user.setPicture(filepath);
        }
        String generatedPassword = StringUtils.generateReferralCode();
        user.setPassword(passwordEncoder.encode(generatedPassword));
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsEnabled(true);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNo());
        user.setBirthDate(request.getBirthDate());
        user.setCreatedDate(ZonedDateTime.now());
        user.setUpdatedDate(ZonedDateTime.now());
        user.setUpdatedBy(createdBy);

        userRepository.save(user);
        List<CsReferral> activeCodes = csReferralRepository.findActiveByUserId(user.getId());
        if(activeCodes.isEmpty()){
            generateNewReferralCode(user.getId());
        }
        return Pair.of(user, generatedPassword);
    }
    public User updateUserAccount(Long userId, SaveProfileRequest request, MultipartFile pictureFile, String updatedBy) throws InvalidRequestException, IOException {
        Optional<User> existing = userRepository.findById(userId);
        if(existing.isEmpty()){
            throw new InvalidRequestException("Not exist user");
        }
        FileUtils fileUtils = new FileUtils();
        User user = existing.get();
        if(pictureFile!=null){
            String filepath = fileUtils.storeFile(pictureFile, pictureBasePath);
            user.setPicture(filepath);
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNo());
        user.setBirthDate(request.getBirthDate());
        user.setUpdatedDate(ZonedDateTime.now());
        user.setUpdatedBy(updatedBy);

        userRepository.save(user);
        List<CsReferral> activeCodes = csReferralRepository.findActiveByUserId(user.getId());
        if(activeCodes.isEmpty()){
            generateNewReferralCode(user.getId());
        }
        return user;
    }

    public User updateProfile(Long userId, SaveProfileRequest request, MultipartFile pictureFile, String updatedBy) throws InvalidRequestException, IOException {
        Optional<User> existing = userRepository.findById(userId);
        if(existing.isEmpty()){
            throw new InvalidRequestException("Not exist user");
        }
        FileUtils fileUtils = new FileUtils();
        User user = existing.get();
        if(pictureFile!=null){
            String filepath = fileUtils.storeFile(pictureFile, pictureBasePath);
            user.setPicture(filepath);
        }
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNo());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setBirthDate(request.getBirthDate());
        user.setUpdatedDate(ZonedDateTime.now());
        user.setUpdatedBy(updatedBy);
        userRepository.save(user);

        List<CsReferral> activeCodes = csReferralRepository.findActiveByUserId(user.getId());
        if(activeCodes.isEmpty()){
            generateNewReferralCode(user.getId());
        }
        return user;
    }

    public boolean signUp(SignupRequest request) throws InvalidRequestException {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("Please to confirm password correctly", null);
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsEnabled(true);
        user.setCreatedDate(ZonedDateTime.now());
        user.setUpdatedDate(ZonedDateTime.now());
        user.setUpdatedBy(request.getEmail());
        userRepository.save(user);
        generateNewReferralCode(user.getId());
        return true;
    }

    public List<UserDto> getUserRefs() {
        List<User> users = userRepository.findAll();
        return users.stream().map(it -> UserDto.builder()
                .id(it.getId())
                .name(it.getName())
                .build()).collect(Collectors.toList());
    }

}
