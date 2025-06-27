package com.merveartut.task_manager;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.UserServiceImpl;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .name("Merve Artut")
                .email("merveartut@gmail.com")
                .password("123456")
                .role(Role.TEAM_LEADER)
                .build();
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode("123456")).thenReturn("encoded123456");
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals("encoded123456", createdUser.getPassword());
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testListUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.listUsers();

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
        assertEquals(user.getName(), users.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void testGetUserById_UserExists() throws UserNotFoundException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUsersByRole() {
        when(userRepository.findByRole(Role.TEAM_MEMBER)).thenReturn(List.of(user));

        List<User> users = userService.getUsersByRole(Role.TEAM_MEMBER);

        assertEquals(1, users.size());
        assertEquals(user.getRole(), users.get(0).getRole());
        verify(userRepository, times(1)).findByRole(Role.TEAM_MEMBER);
    }

    @Test
    void testUpdateUser_Success() throws UserNotFoundException {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(userId, user);

        assertNotNull(updatedUser);
        assertEquals(user.getId(), updatedUser.getId());
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, user));
        verify(userRepository, times(1)).existsById(userId);
    }
    @Test
    void testGetUsersByRole_NoUsersFound() {
        when(userRepository.findByRole(Role.TEAM_MEMBER)).thenReturn(List.of());

        List<User> users = userService.getUsersByRole(Role.TEAM_MEMBER);

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findByRole(Role.TEAM_MEMBER);
    }
    @Test
    void testUpdateUser_UpdatedFields() throws UserNotFoundException {
        User updatedData = User.builder()
                .id(userId)
                .name("Updated Name")
                .role(Role.TEAM_LEADER)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(userId, updatedData);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(Role.TEAM_LEADER, updatedUser.getRole());
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).save(updatedData);
    }

    @Test
    void updateUserEmail_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUserEmail(userId, "aaa@gmail.com");

        assertEquals("aaa@gmail.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void updateUserEmail_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserEmail(userId, "aaa@gmail.com"));
    }

    @Test
    void updateUsername_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUserName(userId, "John");

        assertEquals("John", updatedUser.getName());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void updateUsername_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserName(userId, "John"));
    }
}
