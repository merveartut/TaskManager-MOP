package com.merveartut.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merveartut.task_manager.config.SecurityConfig;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.Todo;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.security.JwtUtil;
import com.merveartut.task_manager.service.TaskService;
import com.merveartut.task_manager.service.TodoService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.TodoNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class TodoControllerTest {

    @MockBean
    private TodoService todoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo todo;
    private UUID todoId;
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

        todo = new Todo();
        todoId = UUID.randomUUID();
        User user = new User();
        userId = UUID.randomUUID();
        user.setId(userId);
        task = new Task();
        taskId = UUID.randomUUID();

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

        todo.setId(todoId);
        todo.setTask(task);
        todo.setText("Find bugs");
        todo.setCompletedState(false);

        teamLeaderToken = "Bearer " + jwtUtil.generateToken("teamLeaderUser", Role.TEAM_LEADER, userId).trim();
        teamMemberToken = "Bearer " + jwtUtil.generateToken("teamMemberUser", Role.TEAM_MEMBER, userId).trim();
        projectManagerToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.PROJECT_MANAGER, userId).trim();
        adminToken = "Bearer " + jwtUtil.generateToken("adminUser", Role.ADMIN, userId).trim();
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void createTodo_Success() throws Exception {
        when(todoService.createTodo(todo)).thenReturn(todo);

        mockMvc.perform(post("/api/todos/v1")
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

    @Test
    void createTodo_TaskNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        todo.getTask().setId(invalidId);
        when(todoService.createTodo(todo)).thenThrow(new TodoNotFoundException());

        mockMvc.perform(post("/api/todos/v1")
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTodosByTask_Success() throws Exception {
        when(todoService.getTodosByTask(taskId)).thenReturn(List.of(todo));

        mockMvc.perform(get("/api/todos/v1")
                        .param("taskId", taskId.toString())
                        .header("Authorization", teamLeaderToken))
                .andExpect(status().isOk());
    }

    @Test
    void getTodosByTask_TaskNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(todoService.getTodosByTask(invalidId)).thenThrow(new TodoNotFoundException());

        mockMvc.perform(get("/api/todos/v1")
                        .param("taskId", invalidId.toString())
                        .header("Authorization", teamLeaderToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void setCompletedState_Success() throws Exception {
        when(todoService.setCompletedState(todoId, true)).thenReturn(todo);

        mockMvc.perform(put("/api/todos/v1/update-state")
                        .header("Authorization", teamLeaderToken)
                        .param("id", todoId.toString())
                        .param("state", String.valueOf(false)))
                .andExpect(status().isOk());
    }


}
