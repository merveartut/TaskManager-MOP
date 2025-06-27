package com.merveartut.task_manager;

import com.merveartut.task_manager.config.SecurityConfig;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Comment;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.security.JwtUtil;
import com.merveartut.task_manager.service.CommentService;
import com.merveartut.task_manager.service.exception.CommentNotFoundException;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class CommentControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtUtil jwtUtil;

    private Comment comment;
    private UUID commentId;
    private Task task;
    private UUID taskId;
    private User commenter;
    private UUID commenterId;

    private String teamLeaderToken;
    private String teamMemberToken;
    private String projectManagerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        comment = new Comment();
        commentId = UUID.fromString("d29bf6e1-f361-4448-b726-504d8847596e");
        comment.setId(commentId);
        task = new Task();
        taskId = UUID.fromString("92caedb3-3eb8-4a93-a7ed-7f7effcd4889");
        task.setId(taskId);
        commenter = new User();
        commenterId = UUID.fromString("8ab10134-45b9-4378-b38c-6d549dfaa7fe");
        commenter.setId(commenterId);

        comment.setId(commentId);
        comment.setTask(task);
        comment.setCommenter(commenter);
        comment.setText("This is a test comment");

        teamLeaderToken = "Bearer " + jwtUtil.generateToken("teamLeaderUser", Role.TEAM_LEADER, commenterId).trim();
        teamMemberToken = "Bearer " + jwtUtil.generateToken("teamMemberUser", Role.TEAM_MEMBER, commenterId).trim();
        projectManagerToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.PROJECT_MANAGER, commenterId).trim();
        adminToken = "Bearer " + jwtUtil.generateToken("adminUser", Role.ADMIN, commenterId).trim();
    }

    @Test
    void createComment_Success() throws Exception {
        when(commentService.createComment(comment)).thenReturn(comment);

        mockMvc.perform(post("/api/comments/v1/add-comment")
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"This is a test comment\", \"task\": {\"id\": \"" + taskId + "\"}, \"commenter\": {\"id\": \"" + commenterId + "\"}}"))
                .andExpect(status().isOk());
    }

    @Test
    void getComments_Success() throws Exception {
        when(commentService.listComments()).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/comments/v1")
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

//    @Test
//    void getComments_Unauthorized() throws Exception {
//        mockMvc.perform(get("/api/comments/v1")
//                        .header("Authorization", teamMemberToken))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void getCommentById_Success() throws Exception{
//        when(commentService.getCommentById(commentId)).thenReturn(comment);
//
//        mockMvc.perform(get("/api/comments/v1/{id}", commentId)
//                        .header("Authorization", projectManagerToken))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getCommentById_CommentNotFound() throws Exception{
//        UUID invalidId = UUID.randomUUID();
//        when(commentService.getCommentById(invalidId)).thenThrow(new CommentNotFoundException());
//
//        mockMvc.perform(get("/api/comments/v1/" + invalidId)
//                        .header("Authorization", projectManagerToken))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getCommentById_Unauthorized() throws Exception{
//        mockMvc.perform(get("/api/comments/v1/" + commentId)
//                        .header("Authorization", teamMemberToken))
//                .andExpect(status().isForbidden());
//    }
//
    @Test
    void getCommentsByTask_Success() throws Exception {
        when(commentService.getCommentsByTask(taskId)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/comments/v1/task?taskId=" + taskId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

    @Test
    void getCommentsByTask_TaskNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(commentService.getCommentsByTask(invalidId)).thenThrow(new TaskNotFoundException());

        mockMvc.perform(get("/api/comments/v1/task?taskId=" + invalidId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }
}
