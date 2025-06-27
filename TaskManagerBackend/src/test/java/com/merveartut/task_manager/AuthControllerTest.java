package com.merveartut.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private String password = "password123";

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("testuser");
        user.setPassword("hashed_password"); // Simulate encoded password
        user.setRole(Role.ADMIN);
    }

    @Test
    void login_Success() throws Exception {
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getName(), user.getRole(), user.getId())).thenReturn("jwt_token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_InvalidPassword() throws Exception {
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", password))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_UserNotFound() throws Exception {
        when(userRepository.findByName("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "unknown", "password", "any"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_Success() throws Exception {
        when(passwordEncoder.encode(password)).thenReturn("encoded_password");
        user.setPassword(password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void changePassword_Success() throws Exception {
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncoded");

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("oldPassword", "oldPassword", "newPassword", "newPassword"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void changePassword_WrongOldPassword() throws Exception {
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("oldPassword", "wrong", "newPassword", "newPassword"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "unknownUser")
    void changePassword_UserNotFound() throws Exception {
        when(userRepository.findByName("unknownUser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("oldPassword", "old", "newPassword", "new"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void guestToken_ReturnsToken() throws Exception {
        when(jwtUtil.generateToken(any(), eq(Role.GUEST), any())).thenReturn("guest_token");

        mockMvc.perform(post("/auth/guest-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("guest_token"))
                .andExpect(jsonPath("$.role").value("GUEST"));
    }
}
