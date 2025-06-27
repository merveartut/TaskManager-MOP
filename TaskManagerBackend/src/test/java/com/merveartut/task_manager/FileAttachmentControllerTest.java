package com.merveartut.task_manager;

import com.merveartut.task_manager.config.SecurityConfig;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.FileAttachment;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.security.JwtUtil;
import com.merveartut.task_manager.service.FileAttachmentService;
import com.merveartut.task_manager.service.exception.CommentNotFoundException;
import com.merveartut.task_manager.service.exception.FileAttachmentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class FileAttachmentControllerTest {

    @MockBean
    private FileAttachmentService fileAttachmentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtUtil jwtUtil;

    private FileAttachment fileAttachment;
    private UUID attachmentId;

    private User user;
    private UUID userId;

    private Task task;

    private UUID taskId;

    private String teamLeaderToken;
    private String teamMemberToken;
    private String projectManagerToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        fileAttachment = new FileAttachment();
        attachmentId = UUID.randomUUID();

        user = new User();
        userId = UUID.randomUUID();
        user.setId(userId);

        task = new Task();
        taskId = UUID.randomUUID();
        task.setId(taskId);

        fileAttachment.setId(attachmentId);
        fileAttachment.setFilePath("/src/main/resources");
        fileAttachment.setUser(user);
        fileAttachment.setTask(task);

        teamLeaderToken = "Bearer " + jwtUtil.generateToken("teamLeaderUser", Role.TEAM_LEADER, userId).trim();
        teamMemberToken = "Bearer " + jwtUtil.generateToken("teamMemberUser", Role.TEAM_MEMBER, userId).trim();
        projectManagerToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.PROJECT_MANAGER, userId).trim();

    }

    @Test
    void createFileAttachment_Success() throws Exception {
        when(fileAttachmentService.createFileAttachment(any(UUID.class), any(FileAttachment.class)))
                .thenReturn(fileAttachment);

        mockMvc.perform(post("/api/attachments/v1")
                        .header("Authorization", projectManagerToken)
                        .param("taskId", taskId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filePath\":\"uploads/test.pdf\", \"task\": {\"id\": \"" + taskId + "\"}, \"user\": {\"id\": \"" + userId + "\"}}"))
                .andExpect(status().isOk());
    }

    @Test
    void getFileAttachments_Success() throws Exception {
    when(fileAttachmentService.listFileAttachments()).thenReturn(List.of(fileAttachment));
        mockMvc.perform(get("/api/attachments/v1")
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

    @Test
    void getFileAttachmentById_Success() throws  Exception{
        when(fileAttachmentService.getFileAttachmentById(attachmentId)).thenReturn(fileAttachment);

        mockMvc.perform(get("/api/attachments/v1/{id}", attachmentId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

    @Test
    void getFileAttachmentById_FileAttachmentNotFound() throws  Exception{
        UUID invalidId = UUID.randomUUID();
        when(fileAttachmentService.getFileAttachmentById(invalidId)).thenThrow(new FileAttachmentNotFoundException());

        mockMvc.perform(get("/api/attachments/v1/{id}", invalidId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFileAttachmentByTaskId_Success() throws  Exception{
        when(fileAttachmentService.getFileAttachmentsByTaskId(taskId)).thenReturn(List.of(fileAttachment));

        mockMvc.perform(get("/api/attachments/v1/task")
                        .param("taskId", taskId.toString())
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

    @Test
    void getFileAttachmentByTaskId_TaskNotFound() throws Exception{
        UUID invalidId = UUID.randomUUID();
        when(fileAttachmentService.getFileAttachmentsByTaskId(invalidId)).thenThrow(new TaskNotFoundException());

        mockMvc.perform(get("/api/attachments/v1/task?taskId=" + invalidId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }

}
