package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, TaskRepository taskRepository, ProjectRepository projectRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        String userId = (String) authentication.getDetails();  // This is where we stored the userId
        return UUID.fromString(userId);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) throws UserNotFoundException{
      return userRepository.findById(id)
               .orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User updateUser(UUID id, User user) throws UserNotFoundException{
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUserEmail(UUID id, String email) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.setEmail(email);
        return userRepository.save(user);
    }

    @Override
    public User updateUserName(UUID id, String name) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.setName(name);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) throws UserNotFoundException {
        List<Task> tasks = taskRepository.findByAssigneeId(id);
        User deletedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        UUID currentUserId = getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException());
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                task.setAssignee(currentUser);
            }

            taskRepository.saveAll(tasks);
        }

        List<Project> relatedProjects = projectRepository.findAllByTeamMemberOrManager(deletedUser);
        for (Project project : relatedProjects) {
            // Remove from teamMembers
            project.getTeamMembers().remove(deletedUser);

            // If the user is the project manager, unset them
            if (deletedUser.equals(project.getProjectManager())) {
                project.setProjectManager(currentUser); // or reassign to another user if needed
            }
        }
        projectRepository.saveAll(relatedProjects);

        userRepository.deleteById(id);
    }

}
