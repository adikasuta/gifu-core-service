package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.CsReferral;
import com.gifu.coreservice.entity.Role;
import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.UserDto;
import com.gifu.coreservice.model.dto.ValueTextDto;
import com.gifu.coreservice.model.request.SaveProfileRequest;
import com.gifu.coreservice.model.request.SearchUserRequest;
import com.gifu.coreservice.model.request.SignupRequest;
import com.gifu.coreservice.repository.CsReferralRepository;
import com.gifu.coreservice.repository.RoleRepository;
import com.gifu.coreservice.repository.UserRepository;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import com.gifu.coreservice.utils.FileUtils;
import com.gifu.coreservice.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private RoleRepository roleRepository;
    @Autowired
    private CsReferralRepository csReferralRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${picture.path}")
    private String pictureBasePath;

    public List<ValueTextDto> getRolesReference() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(it -> new ValueTextDto(String.valueOf(it.getId()), it.getName())).collect(Collectors.toList());
    }

    public Page<UserDto> searchUser(SearchUserRequest request, Pageable pageable) {
        BasicSpec<User> nameLike = new BasicSpec<>(new SearchCriteria("name", SearchOperation.LIKE, request.getSearchQuery()));
        BasicSpec<User> emailLike = new BasicSpec<>(new SearchCriteria("email", SearchOperation.LIKE, request.getSearchQuery()));
        BasicSpec<User> employeeCodeLike = new BasicSpec<>(new SearchCriteria("employeeCode", SearchOperation.LIKE, request.getSearchQuery()));
        Specification<User> searchQuery = Specification.where(nameLike).or(emailLike).or(employeeCodeLike);
        if (request.getRoleId() != null) {
            BasicSpec<User> roleIdEquals = new BasicSpec<>(new SearchCriteria("roleId", SearchOperation.EQUALS, request.getRoleId()));
            searchQuery.and(roleIdEquals);
        }
        Page<User> userPage = userRepository.findAll(searchQuery, pageable);
        return userPage.map(it -> {
            UserDto mapped = UserDto.builder().id(it.getId())
                    .name(it.getName())
                    .active(it.getIsAccountNonExpired() && it.getIsAccountNonLocked() && it.getIsCredentialsNonExpired() && it.getIsEnabled())
                    .build();
            if (it.getRoleId() != null) {
                Optional<Role> roleOpt = roleRepository.findById(it.getRoleId());
                roleOpt.ifPresent(role -> {
                    mapped.setRoleName(role.getName());
                });
            }
            return mapped;
        });
    }

    public CsReferral generateNewReferralCode(Long userId) {
        String token = StringUtils.generateReferralCode();
        long existingCount = csReferralRepository.countByToken(token);
        if (existingCount > 0) {
            generateNewReferralCode(userId);
        }
        List<CsReferral> legacyCodes = csReferralRepository.findActiveByUserId(userId);
        for (CsReferral it : legacyCodes) {
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
        if (pictureFile != null) {
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
        user.setRoleId(request.getRoleId());

        userRepository.save(user);
        List<CsReferral> activeCodes = csReferralRepository.findActiveByUserId(user.getId());
        if (activeCodes.isEmpty()) {
            generateNewReferralCode(user.getId());
        }
        return Pair.of(user, generatedPassword);
    }

    public User updateUserAccount(Long userId, SaveProfileRequest request, MultipartFile pictureFile, String updatedBy) throws InvalidRequestException, IOException {
        Optional<User> existing = userRepository.findById(userId);
        if (existing.isEmpty()) {
            throw new InvalidRequestException("Not exist user");
        }
        FileUtils fileUtils = new FileUtils();
        User user = existing.get();
        if (pictureFile != null) {
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
        if (activeCodes.isEmpty()) {
            generateNewReferralCode(user.getId());
        }
        return user;
    }

    public User updateProfile(Long userId, SaveProfileRequest request, MultipartFile pictureFile, String updatedBy) throws InvalidRequestException, IOException {
        Optional<User> existing = userRepository.findById(userId);
        if (existing.isEmpty()) {
            throw new InvalidRequestException("Not exist user");
        }
        FileUtils fileUtils = new FileUtils();
        User user = existing.get();
        if (pictureFile != null) {
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
        if (activeCodes.isEmpty()) {
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
