package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.Permission;
import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.filter.JwtFilter;
import com.gifu.coreservice.model.response.LoginResponse;
import com.gifu.coreservice.repository.UserRepository;
import com.gifu.coreservice.service.AuthService;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.tomcat.util.json.JSONParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthController authController;
    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapperService objectMapperService;

    @BeforeEach
    public void setUp(){
        Permission permission1 = new Permission();
        permission1.setCode("permission_1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission1);

        User user1 = new User();
        user1.setEmail("some@email.com");
        user1.setPassword(passwordEncoder.encode("somepassword"));
        user1.setIsAccountNonExpired(true);
        user1.setIsAccountNonLocked(true);
        user1.setIsCredentialsNonExpired(true);
        user1.setIsEnabled(true);
        user1.setPermissions(permissions);
        userRepository.save(user1);

        User inactiveUser = new User();
        inactiveUser.setEmail("someinactive@email.com");
        inactiveUser.setPassword(passwordEncoder.encode("someinactivepassword"));
        inactiveUser.setIsAccountNonExpired(true);
        inactiveUser.setIsAccountNonLocked(false);
        inactiveUser.setIsCredentialsNonExpired(true);
        inactiveUser.setIsEnabled(true);
        userRepository.save(user1);
    }

    @AfterEach
    public void cleanUp(){
        userRepository.deleteAll();
    }

    @Test
    public void shouldAbleToChangePassword() throws Exception {
        User user = userRepository.findByEmail("some@email.com").get();
        String jwt = JwtUtils.createJwtSignedHMAC(user);
        MvcResult result = mockMvc.perform(
                        post("/auth/api/change-password")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", "Bearer "+jwt)
                                .content("{\n" +
                                        "  \"oldPassword\":\"somepassword\",\n" +
                                        "  \"newPassword\":\"newpassword\"\n" +
                                        "}")
                ).andExpect(status().isOk())
                .andReturn();
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("some@email.com", "newpassword"));
        User changed = (User) authenticate.getPrincipal();
        assertThat(changed.getEmail()).isEqualTo("some@email.com");
    }

    @Test
    public void shouldNotAbleToChangePassword_whenJwtIsNotValid() throws Exception {
        String jwt = "invalid JWT";
        MvcResult result = mockMvc.perform(
                        post("/auth/api/change-password")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", "Bearer "+jwt)
                                .content("{\n" +
                                        "  \"oldPassword\":\"somepassword\",\n" +
                                        "  \"newPassword\":\"newpassword\"\n" +
                                        "}")
                ).andExpect(status().is4xxClientError())
                .andReturn();
        String error = null;
        try{
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("some@email.com", "newpassword"));
        }catch (AuthenticationException e){
            error = e.getMessage();
        }
        assertThat(error).isEqualTo("Bad credentials");
    }

    @Test
    public void shouldAbleToLogin() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/auth/api/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"email\":\"some@email.com\",\n" +
                                "  \"password\":\"somepassword\",\n" +
                                "  \"redirectUrl\":\"\"\n" +
                                "}")
        ).andExpect(status().isOk())
                        .andReturn();
        Map<String,Object> res = objectMapperService.readToObject(result.getResponse().getContentAsString(),Map.class);
        String loginResJson = objectMapperService.writeToString(res.get("data"));
        LoginResponse loginResponse = objectMapperService.readToObject(loginResJson,LoginResponse.class);
        Jws<Claims> parsed = JwtUtils.parseJwt(loginResponse.getToken());
        assertThat(parsed.getBody().get("email")).isEqualTo("some@email.com");
    }

    @Test
    public void shouldReject_whenInputWrongPassword() throws Exception {
        mockMvc.perform(
                        post("/auth/api/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{\n" +
                                        "  \"email\":\"some@email.com\",\n" +
                                        "  \"password\":\"wrongpassword\",\n" +
                                        "  \"redirectUrl\":\"\"\n" +
                                        "}")
                ).andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReject_whenInputInactiveUser() throws Exception {
        mockMvc.perform(
                        post("/auth/api/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{\n" +
                                        "  \"email\":\"someinactive@email.com\",\n" +
                                        "  \"password\":\"someinactivepassword\",\n" +
                                        "  \"redirectUrl\":\"\"\n" +
                                        "}")
                ).andExpect(status().is4xxClientError());
    }
}