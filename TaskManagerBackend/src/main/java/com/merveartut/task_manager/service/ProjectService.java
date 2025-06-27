package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(Project project);
    List<Project> getProjects();
    Project getProjectById(UUID id) throws ProjectNotFoundException;
    List<Project> getProjectsByTeamMember(UUID userId) throws UserNotFoundException;
    List<Project> getProjectsByManager(UUID userId) throws UserNotFoundException;
    Project updateProject(Project project) throws ProjectNotFoundException;
    Project setProjectManager(UUID projectId, UUID userId) throws UserNotFoundException;
    List<User> getProjectTeamMembers(UUID id) throws ProjectNotFoundException;
    void deleteProject(UUID id) throws ProjectNotFoundException;
}
