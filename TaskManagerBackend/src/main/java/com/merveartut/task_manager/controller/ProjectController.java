package com.merveartut.task_manager.controller;

import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.service.ProjectService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/v1")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUEST')")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @GetMapping("/v1")
    public ResponseEntity<List<Project>> getProjects() {
        return ResponseEntity.ok(projectService.getProjects());
    }

    @GetMapping("/v1/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/v1/by-manager")
    public ResponseEntity<List<Project>> getProjectsByManager(@RequestParam UUID userId) {
        return ResponseEntity.ok(projectService.getProjectsByManager(userId));
    }

    @GetMapping("/v1/by-team-member")
    public ResponseEntity<List<Project>> getProjectsByTeamMember(@RequestParam UUID userId) {
        return ResponseEntity.ok(projectService.getProjectsByTeamMember(userId));
    }

    @GetMapping("/v1/team-members")
    public ResponseEntity<List<User>> getProjectTeamMembers(@RequestParam UUID id) {
        return ResponseEntity.ok(projectService.getProjectTeamMembers(id));
    }

    @PutMapping("/v1")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUEST')")
    public ResponseEntity<Project> updateProject(@RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(project));
    }

    @PutMapping("/v1/set-project-manager")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUEST')")
    public ResponseEntity<Project> setProjectManager(@RequestParam UUID projectId, @RequestParam UUID userId) {
        return ResponseEntity.ok(projectService.setProjectManager(projectId, userId));
    }

    @DeleteMapping("/v1")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUEST')")
    public ResponseEntity<Void> deleteProject(@RequestParam UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException ex) {
        return ResponseEntity.status(404).body("Project not found");
    }
}
