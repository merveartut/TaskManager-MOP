package com.merveartut.task_manager;

import com.merveartut.task_manager.model.Comment;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.CommentRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.CommentServiceImpl;
import com.merveartut.task_manager.service.exception.CommentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private Comment comment;
    private Task task;
    private UUID commentId;
    private UUID taskId;
    private User commenter;
    private UUID commenterId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        commenterId = UUID.randomUUID();

        task = new Task();
        task.setId(taskId);

        commenter = new User();
        commenter.setId(commenterId);

        comment = Comment.builder()
                .id(commentId)
                .commenter(commenter)
                .task(task)
                .text("This is a test comment")
                .createdAt(LocalDateTime.now())
                .build();

    }

    @Test
    void testCreateComment_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(commenterId)).thenReturn(Optional.of(commenter));
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment createdComment = commentService.createComment(comment);

        assertNotNull(createdComment);
        assertEquals(taskId, createdComment.getTask().getId());
        assertEquals(comment.getText(), createdComment.getText());

        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(commenterId);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testCreateComment_TaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> commentService.createComment(comment));

        verify(taskRepository, times(1)).findById(taskId);
        verify(commentRepository, never()).save(any());
    }
    @Test
    void testListComments() {
        when(commentRepository.findAll()).thenReturn(List.of(comment));

        List<Comment> comments = commentService.listComments();

        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());

        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void testGetCommentById_Success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment foundComment = commentService.getCommentById(commentId);

        assertNotNull(foundComment);
        assertEquals(commentId, foundComment.getId());

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void testGetCommentById_CommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(commentId));

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void testGetCommentsByTask() {
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(commentRepository.findByTaskId(taskId)).thenReturn(List.of(comment));

        List<Comment> comments = commentService.getCommentsByTask(taskId);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(taskId, comments.get(0).getTask().getId());

        verify(commentRepository, times(1)).findByTaskId(taskId);
    }

    @Test
    void testGetCommentByTask_TaskNotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> commentService.getCommentsByTask(taskId));

        verify(commentRepository, never()).findByTaskId(any());
    }

    @Test
    void testUpdateComment_Success() {
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(commentRepository.save(comment)).thenReturn(comment);

        comment.setText("Text is updated.");

        Comment updatedComment = commentService.updateComment(commentId, comment);

        assertNotNull(updatedComment);
        assertEquals("Text is updated.", updatedComment.getText());

        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testUpdateComment_CommentNotFound() {
        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(commentId, comment));

        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, never()).save(any());
    }


}
