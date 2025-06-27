package com.merveartut.task_manager.controller;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.service.UserService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/v1")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/v1")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @GetMapping("/v1/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/v1/role")
    public ResponseEntity<List<User>> getUserByRole(@RequestParam Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/v1/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @PutMapping("/v1/update-email")
    public ResponseEntity<User> updateUserEmail(@RequestParam UUID id, @RequestParam String email) {
        return ResponseEntity.ok(userService.updateUserEmail(id, email));
    }

    @PutMapping("/v1/update-name")
    public ResponseEntity<User> updateUserName(@RequestParam UUID id, @RequestParam String name) {
        return ResponseEntity.ok(userService.updateUserName(id, name));
    }

    @DeleteMapping("/v1")
    public ResponseEntity<Void> deleteUser(@RequestParam UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(404).body("Project not found");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404).body("User not found");
    }
}