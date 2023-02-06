package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.filter.JwtFilter;
import com.gifu.coreservice.repository.UserRepository;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapperService objectMapperService;

    @BeforeAll
    public void setUp(){
        User user1 = new User();
        user1.setEmail("some@email.com");
        user1.setPassword(passwordEncoder.encode("somepassword"));
        user1.setIsAccountNonExpired(true);
        user1.setIsAccountNonLocked(true);
        user1.setIsCredentialsNonExpired(true);
        user1.setIsEnabled(true);
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

    @Test
    public void shouldAbleToLogin() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"email\":\"some@email.com\",\n" +
                                "  \"password\":\"somepassword\",\n" +
                                "  \"redirectUrl\":\"\"\n" +
                                "}")
        ).andExpect(status().isOk())
                        .andReturn();
        Map<String,String> res = objectMapperService.readToObject(result.getResponse().getContentAsString(),Map.class);
        Jws<Claims> parsed = JwtUtils.parseJwt(res.get("token"));
        assertThat(parsed.getBody().get("email")).isEqualTo("some@email.com");
    }

    @Test
    public void shouldReject_whenInputWrongPassword() throws Exception {
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{\n" +
                                        "  \"email\":\"some@email.com\",\n" +
                                        "  \"password\":\"wrongpassword\",\n" +
                                        "  \"redirectUrl\":\"\"\n" +
                                        "}")
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string(Matchers.emptyOrNullString()));
    }

    @Test
    public void shouldReject_whenInputInactiveUser() throws Exception {
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{\n" +
                                        "  \"email\":\"someinactive@email.com\",\n" +
                                        "  \"password\":\"someinactivepassword\",\n" +
                                        "  \"redirectUrl\":\"\"\n" +
                                        "}")
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string(Matchers.emptyOrNullString()));
    }
}