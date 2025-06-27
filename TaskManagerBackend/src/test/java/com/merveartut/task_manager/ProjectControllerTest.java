package com.merveartut.task_manager;

import com.merveartut.task_manager.config.SecurityConfig;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.security.JwtUtil;
import com.merveartut.task_manager.service.ProjectService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtUtil jwtUtil;

    private Project project;
    private UUID projectId;
    private UUID userId;

    private String teamLeaderToken;
    private String teamMemberToken;
    private String projectManagerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        project = new Project();
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();
        project.setId(projectId);
        project.setTitle("Test Project");
        project.setDescription("This is a test project.");
        project.setDepartmentName("Test");
        project.setTeamMembers(null);
        project.setTasks(null);

        teamLeaderToken = "Bearer " + jwtUtil.generateToken("teamLeaderUser", Role.TEAM_LEADER, userId).trim();
        teamMemberToken = "Bearer " + jwtUtil.generateToken("teamMemberUser", Role.TEAM_MEMBER, userId).trim();
        projectManagerToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.PROJECT_MANAGER, userId).trim();
        adminToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.ADMIN, userId).trim();

    }

    @Test
     void createProject_Success() throws Exception {
        when(projectService.createProject(project)).thenReturn(project);

        mockMvc.perform(post("/api/projects/v1")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Project\"}"))
                .andExpect(status().isOk());
   }

    @Test
    void createProject_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/projects/v1")
                        .header("Authorization", teamMemberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Project\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProjects_Success() throws Exception {
        when(projectService.getProjects()).thenReturn(List.of(project));

        mockMvc.perform(get("/api/projects/v1")
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

//    @Test
//    void getProjects_Unauthorized() throws Exception {
//        mockMvc.perform(get("/api/projects/v1")
//                        .header("Authorization", teamMemberToken))
//                .andExpect(status().isForbidden());
//    }
//
    @Test
    void getProjectById_Success() throws Exception {
        when(projectService.getProjectById(projectId)).thenReturn(project);

        mockMvc.perform(get("/api/projects/v1/{id}", projectId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.title").value("Test Project"));
    }
    @Test
    public void getProjectById_ProjectNotFound() throws Exception{
        UUID invalidId = UUID.randomUUID();
        when(projectService.getProjectById(invalidId)).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/api/projects/v1/" + invalidId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }
    @Test
    void getProjectByManager_Success() throws Exception {
        when(projectService.getProjectsByManager(userId)).thenReturn(List.of(project));

        mockMvc.perform(get("/api/projects/v1/by-manager")
                        .param("userId", userId.toString())
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }
    @Test
    public void getProjectByManager_ProjectNotFound() throws Exception{
        UUID invalidId = UUID.randomUUID();
        when(projectService.getProjectsByManager(invalidId)).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/api/projects/v1/by-manager")
                        .param("userId", invalidId.toString())
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjectByTeamMember_Success() throws Exception {
        when(projectService.getProjectsByTeamMember(userId)).thenReturn(List.of(project));

        mockMvc.perform(get("/api/projects/v1/by-team-member")
                        .param("userId", userId.toString())
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }
    @Test
    public void getProjectByTeamMember_ProjectNotFound() throws Exception{
        UUID invalidId = UUID.randomUUID();
        when(projectService.getProjectsByTeamMember(invalidId)).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/api/projects/v1/by-team-member")
                        .param("userId", invalidId.toString())
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void getProjectById_Unauthorized() throws Exception {
//        when(projectService.getProjectById(any(UUID.class))).thenReturn(project);
//
//        mockMvc.perform(get("/api/projects/v1", projectId)
//                        .header("Authorization", teamMemberToken))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void updateProject_Success() throws Exception {
        project.setTitle("Updated Project");

        when(projectService.updateProject(project)).thenReturn(project);

        mockMvc.perform(put("/api/projects/v1", projectId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + projectId + "\", \"title\":\"Updated Project\"}"))
                .andExpect(status().isOk());
    }
    @Test
    public void updateProject_ProjectNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(projectService.updateProject(any(Project.class))).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(put("/api/projects/v1", invalidId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Project\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProject_Unauthorized() throws Exception {
        mockMvc.perform(put("/api/projects/v1", projectId)
                        .header("Authorization", teamMemberToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Project\"}"))
                       .andExpect(status().isForbidden());
    }

    @Test
    void setProjectManager_Success() throws Exception {
        when(projectService.setProjectManager(projectId, userId)).thenReturn(project);

        mockMvc.perform(put("/api/projects/v1/set-project-manager")
                        .param("projectId", projectId.toString())
                        .param("userId", userId.toString())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()));

    }

    @Test
    void setProjectManager_Unauthorized() throws Exception {
        mockMvc.perform(put("/api/projects/v1/set-project-manager", projectId, userId)
                        .header("Authorization", teamMemberToken))
                .andExpect(status().isForbidden());
        verifyNoInteractions(projectService);
    }

    @Test
    void deleteProject_Success() throws Exception {
        doNothing().when(projectService).deleteProject(projectId);

        mockMvc.perform(delete("/api/projects/v1")
                        .param("id", projectId.toString())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProject(projectId);
    }

    @Test
    void deleteProject_ForbiddenForUnauthorizedUser() throws Exception {
        mockMvc.perform(delete("/api/projects/v1")
                        .param("id", projectId.toString())
                .header("Authorization", teamMemberToken))
                .andExpect(status().isForbidden());

        verifyNoInteractions(projectService);
    }
}
