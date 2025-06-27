package com.merveartut.task_manager;

import com.merveartut.task_manager.enums.ProjectStatus;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.ProjectServiceImpl;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    private Project project;
    private UUID projectId;
    private User user;
    private User user2;
    private UUID userId;
    private UUID userId2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User();
        user2 = new User();
        user.setId(userId);
        user.setName("Alice");
        user.setRole(Role.TEAM_LEADER);
        user2.setId(userId2);
        user2.setRole(Role.TEAM_MEMBER);
        user2.setName("Bob");

        project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .description("This is a test project.")
                .departmentName("Test")
                .teamMembers(List.of(user, user2))
                .projectManager(user)
                .tasks(null)
                .build();
    }

    @Test
    void testCreateProject() {
        when(projectRepository.save(project)).thenReturn(project);

        Project createdProject = projectService.createProject(project);

        assertNotNull(createdProject);
        assertEquals(project.getId(), createdProject.getId());
        assertEquals(project.getTitle(), createdProject.getTitle());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testCreateProject_SetStatusToInProgress() {
        when(projectRepository.save(project)).thenReturn(project);

        Project createdProject = projectService.createProject(project);

        assertNotNull(createdProject);
        assertEquals(ProjectStatus.IN_PROGRESS, createdProject.getStatus());
        verify(projectRepository, times(1)).save(project);
    }
    @Test
    void testListProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<Project> projects = projectService.getProjects();

        assertEquals(1, projects.size());
        assertEquals(project, projects.get(0));
        assertEquals(project.getId(), projects.get(0).getId());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectById_Success() throws UserNotFoundException {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project foundProject = projectService.getProjectById(projectId);

        assertNotNull(foundProject);
        assertEquals(projectId, foundProject.getId());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetProjectById_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(projectId));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void getProjectTeamMembers_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        List<User> teamMembers = projectService.getProjectTeamMembers(projectId);

        assertNotNull(teamMembers);
        assertEquals(2, teamMembers.size());
        assertTrue(teamMembers.contains(user));
        assertTrue(teamMembers.contains(user2));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void getProjectTeamMembers_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> {
            projectService.getProjectTeamMembers(projectId);
        });

        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetProjectByManager_Success()  {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findByManagerId(userId)).thenReturn(List.of(project));

        List<Project> foundProjects = projectService.getProjectsByManager(userId);

        assertNotNull(foundProjects);
        assertEquals(project, foundProjects.get(0));
        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).findByManagerId(userId);
    }


    @Test
    void testGetProjectByManager_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> projectService.getProjectsByManager(userId));
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void testGetProjectByTeamMember_Success()  {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findAllByTeamMemberOrManager(user)).thenReturn(List.of(project));

        List<Project> foundProjects = projectService.getProjectsByTeamMember(userId);

        assertNotNull(foundProjects);
        assertEquals(project, foundProjects.get(0));
        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).findAllByTeamMemberOrManager(user);
    }


    @Test
    void testGetProjectByTeamMember_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> projectService.getProjectsByTeamMember(userId));
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void testUpdateProject_Success() throws UserNotFoundException {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("username", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        authentication.setDetails(project.getProjectManager().getId().toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        Project updatedProject = projectService.updateProject(project);

        assertNotNull(updatedProject);

        verify(projectRepository, times(1)).save(project);
    }
    @Test
    void testUpdateProject_ProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(project));
    }

    @Test
    void testSetProjectManager_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        Project updatedProject = projectService.setProjectManager(projectId, userId);

        assertNotNull(updatedProject);
        assertEquals(user, updatedProject.getProjectManager());

        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testSetProjectManager_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> projectService.setProjectManager(projectId, userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(projectRepository);

    }

    @Test
    void testSetProjectManager_ProjectNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.setProjectManager(projectId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testDeleteProject_Success() {
        when(projectRepository.existsById(projectId)).thenReturn(true);

        projectService.deleteProject(projectId);

        verify(projectRepository, times(1)).existsById(projectId);
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    void testDeleteProject_ProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(projectId));

        verify(projectRepository, times(1)).existsById(projectId);
        verify(projectRepository, never()).deleteById(any());
    }
}
