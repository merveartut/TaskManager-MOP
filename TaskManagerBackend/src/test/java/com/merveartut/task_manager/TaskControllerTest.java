package com.merveartut.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merveartut.task_manager.config.SecurityConfig;
import com.merveartut.task_manager.controller.TaskController;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.security.JwtUtil;
import com.merveartut.task_manager.service.TaskService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class TaskControllerTest {

    @MockBean
    private TaskService  taskService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;
    private UUID taskId;
    private Project project;
    private UUID projectId;
    private UUID userId;
    private String teamLeaderToken;
    private String teamMemberToken;
    private String adminToken;
    private String projectManagerToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        task = new Task();
        taskId = UUID.randomUUID();
        User assignee = new User();
        userId = UUID.randomUUID();
        assignee.setId(userId);
        task.setAssignee(assignee);
        project = new Project();
        projectId = UUID.randomUUID();

        project.setId(projectId);
        project.setTitle("Test Project");
        project.setDescription("This is a test project.");
        project.setDepartmentName("Test");

        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setState(TaskState.BACKLOG);
        task.setPriority(TaskPriority.LOW);
        task.setProject(project);

        teamLeaderToken = "Bearer " + jwtUtil.generateToken("teamLeaderUser", Role.TEAM_LEADER, userId).trim();
        teamMemberToken = "Bearer " + jwtUtil.generateToken("teamMemberUser", Role.TEAM_MEMBER, userId).trim();
        projectManagerToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.PROJECT_MANAGER, userId).trim();
        adminToken = "Bearer " + jwtUtil.generateToken("adminUser", Role.ADMIN, userId).trim();
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
     void createTask_Success() throws Exception {
        when(taskService.createTask(task)).thenReturn(task);

        mockMvc.perform(post("/api/tasks/v1")
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk());
    }

    @Test
    void createTask_ProjectNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        task.getProject().setId(invalidId);
        when(taskService.createTask(task)).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(post("/api/tasks/v1")
                .header("Authorization", projectManagerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void createTask_Unauthorized() throws Exception{
//        mockMvc.perform(post("/api/tasks/v1")
//                        .header("Authorization", teamLeaderToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(task)))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void getTasks_Success() throws Exception{
        when(taskService.listTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks/v1")
                .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

//    @Test
//    void getTasks_Unauthorized() throws Exception {
//        mockMvc.perform(get("/api/tasks/v1")
//                .header("Authorization", teamLeaderToken))
//                .andExpect(status().isForbidden());
//    }
//
    @Test
    void getTaskById_Success() throws Exception {
        when(taskService.getTaskById(taskId)).thenReturn(task);

         mockMvc.perform(get("/api/tasks/v1/task/{id}", taskId)
                 .header("Authorization", teamLeaderToken))
                 .andExpect(jsonPath("$.id").value(taskId.toString()))
                 .andExpect(status().isOk());
    }

    @Test
    void getTaskById_TaskNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(taskService.getTaskById(invalidId)).thenThrow(new TaskNotFoundException());

        mockMvc.perform(get("/api/tasks/v1/task/{id}", invalidId)
                .header("Authorization", teamLeaderToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksByProjectId_Success() throws Exception {
        when(taskService.getTasksByProjectId(projectId)).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks/v1/project/{projectId}", projectId)
                        .header("Authorization", teamLeaderToken))
                .andExpect(status().isOk());
    }

    @Test
    void getTasksByProjectId_ProjectNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(taskService.getTasksByProjectId(invalidId)).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/api/tasks/project", invalidId)
                        .header("Authorization", teamLeaderToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksByUserId_Success() throws Exception {
        when(taskService.getTasksByAssignee(taskId)).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks/v1/user/{userId}", userId)
                        .header("Authorization", teamLeaderToken))
                .andExpect(status().isOk());
    }

    @Test
    void getTasksByUserId_UserNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(taskService.getTasksByAssignee(invalidId)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/api/tasks/v1/user/{userId}", invalidId)
                        .header("Authorization", teamLeaderToken))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void getTasksByProjectId_Unauthorized() throws Exception{
//        mockMvc.perform(get("/api/tasks/v1/project/" + projectId)
//                        .header("Authorization", teamMemberToken))
//                .andExpect(status().isForbidden());
//    }
//

    @Test
    void updateTask_Success() throws Exception {
        task.setTitle("Updated Project");

        when(taskService.updateTask(task)).thenReturn(task);

        mockMvc.perform(put("/api/tasks/v1/update", taskId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + taskId + "\", \"title\":\"Updated Task\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTask_TaskNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(taskService.updateTask(any(Task.class))).thenThrow(new TaskNotFoundException());

        mockMvc.perform(put("/api/tasks/v1/update", invalidId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Task\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_Unauthorized() throws Exception {
        mockMvc.perform(put("/api/tasks/v1/update", taskId)
                        .header("Authorization", teamMemberToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Task\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void setTaskAssignee_Success() throws Exception {
        when(taskService.setTaskAssignee(taskId, userId)).thenReturn(task);

        mockMvc.perform(put("/api/tasks/v1/set-assignee")
                        .header("Authorization", teamLeaderToken)
                        .param("id", taskId.toString())
                        .param("userId", userId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void setTaskAssignee_UserNotFound() throws Exception {
        UUID invalidUserId = UUID.randomUUID();
        when(taskService.setTaskAssignee(taskId, invalidUserId)).thenThrow(new UserNotFoundException());

        mockMvc.perform(put("/api/tasks/v1/set-assignee")
                        .header("Authorization", teamLeaderToken)
                        .param("id", taskId.toString())
                        .param("userId", invalidUserId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void setTaskAssignee_Unauthorized() throws Exception{
        UUID invalidUserId = UUID.randomUUID();
        mockMvc.perform(put("/api/tasks/v1/set-assignee")
                        .header("Authorization", teamMemberToken)
                .param("id", taskId.toString())
                .param("userId", invalidUserId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void setTaskPriority_Success() throws Exception {
        when(taskService.setTaskPriority(taskId, TaskPriority.MEDIUM)).thenReturn(task);

        mockMvc.perform(put("/api/tasks/v1/set-priority")
                        .header("Authorization", teamLeaderToken)
                .param("id", taskId.toString())
                .param("priority", TaskPriority.MEDIUM.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void setTaskPriority_Unauthorized() throws Exception {
        mockMvc.perform(put("/api/tasks/v1/set-priority")
                        .header("Authorization", teamMemberToken)
                .param("id", taskId.toString())
                .param("priority", TaskPriority.MEDIUM.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void setTaskState_Success() throws Exception {
        when(taskService.setTaskState(taskId, TaskState.IN_ANALYSIS, null)).thenReturn(task);

        mockMvc.perform(put("/api/tasks/v1/set-state")
                        .header("Authorization", teamMemberToken)
                .param("id", taskId.toString())
                .param("state", TaskState.IN_ANALYSIS.toString())
                .param("reason", ""))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/api/tasks/v1")
                        .param("id", taskId.toString())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    void deleteTask_ForbiddenForUnauthorizedUser() throws Exception {
        mockMvc.perform(delete("/api/tasks/v1")
                        .param("id", taskId.toString())
                        .header("Authorization", teamMemberToken))
                .andExpect(status().isForbidden());

        verifyNoInteractions(taskService);
    }

    @Test
    public void deleteTask_TaskNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        doThrow(new TaskNotFoundException()).when(taskService).deleteTask(invalidId);

        mockMvc.perform(delete("/api/tasks/v1")
                        .param("id", invalidId.toString())
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

}
