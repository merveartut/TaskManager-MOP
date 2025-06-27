package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.ProjectStatus;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        String userId = (String) authentication.getDetails();  // This is where we stored the userId
        return UUID.fromString(userId);
    }

    public Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        String roleStr = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // e.g., "ROLE_ADMIN"
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No role assigned"));

        return Role.valueOf(roleStr.replace("ROLE_", "")); // Convert back to your enum
    }

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }
    @Override
    public Project createProject(Project project) {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(UUID id) throws ProjectNotFoundException{
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException());
    }


    @Override
    public List<Project> getProjectsByManager(UUID userId) throws UserNotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        return projectRepository.findByManagerId(userId);
    }

    @Override
    public List<Project> getProjectsByTeamMember(UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        return projectRepository.findAllByTeamMemberOrManager(user);
    }

    @Override
    public Project updateProject(Project project) throws ProjectNotFoundException{
        Project existingProject = projectRepository.findById(project.getId())
                .orElseThrow(ProjectNotFoundException::new);

        UUID currentUserId = getCurrentUserId();
        Role userRole = getCurrentUserRole();

        User projectManager = existingProject.getProjectManager();

        boolean isProjectManager = projectManager != null && projectManager.getId().equals(currentUserId);
        boolean isAdmin = userRole == Role.ADMIN;
        boolean isGuest = userRole == Role.GUEST;

        if (!isAdmin && !isProjectManager && !isGuest) {
            throw new AccessDeniedException("Only the assigned project manager or an admin or guest can update this project.");
        }
        existingProject.setTitle(project.getTitle());
        existingProject.setDescription(project.getDescription());
        existingProject.setStatus(project.getStatus());
        existingProject.setDepartmentName(project.getDepartmentName());
        existingProject.setTeamMembers(project.getTeamMembers());

        return projectRepository.save(existingProject);
    }

    @Override
    public Project setProjectManager(UUID projectId, UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException());
        project.setProjectManager(user);
        return projectRepository.save(project);
    }

    @Override
    public List<User> getProjectTeamMembers(UUID id) throws ProjectNotFoundException{
        Project project = projectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);
        return project.getTeamMembers();
    }

    @Override
    public void deleteProject(UUID id) throws ProjectNotFoundException {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException();
        }
        projectRepository.deleteById(id);
    }

}
