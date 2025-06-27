package com.merveartut.task_manager;

import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.FileAttachment;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.FileAttachmentRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.FileAttachmentServiceImpl;
import com.merveartut.task_manager.service.exception.FileAttachmentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileAttachmentServiceTest {

    @InjectMocks
    private FileAttachmentServiceImpl fileAttachmentService;

    @Mock
    private FileAttachmentRepository fileAttachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private FileAttachment fileAttachment;
    private UUID fileAttachmentId;

    private Task task;
    private User user;
    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileAttachmentId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();

        task = new Task();
        task.setId(taskId);

        user = new User();
        user.setId(userId);

        fileAttachment = FileAttachment.builder()
                .id(fileAttachmentId)
                .task(task)
                .user(user)
                .filePath("/test/com/merveartut/task_manager/files/file.txt")
                .build();
    }

    @Test
    void testCreateFileAttachment_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(fileAttachmentRepository.save(fileAttachment)).thenReturn(fileAttachment);

        FileAttachment savedFile = fileAttachmentService.createFileAttachment(taskId, fileAttachment);

        assertNotNull(savedFile);
        assertEquals(taskId, savedFile.getTask().getId());
        verify(fileAttachmentRepository, times(1)).save(fileAttachment);
    }

    @Test
    void testCreateFileAttachment_TaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> fileAttachmentService.createFileAttachment(taskId, fileAttachment));

        verify(fileAttachmentRepository, never()).save(any());
    }

    @Test
    void testGetFileAttachmentById_Success() {
        when(fileAttachmentRepository.findById(fileAttachmentId)).thenReturn(Optional.of(fileAttachment));

        FileAttachment foundFile = fileAttachmentService.getFileAttachmentById(fileAttachmentId);

        assertNotNull(foundFile);
        assertEquals(fileAttachmentId, foundFile.getId());
    }

    @Test
    void testGetFileAttachmentById_NotFound() {
        when(fileAttachmentRepository.findById(fileAttachmentId)).thenReturn(Optional.empty());

        assertThrows(FileAttachmentNotFoundException.class, () -> fileAttachmentService.getFileAttachmentById(fileAttachmentId));
    }

    @Test
    void testListFileAttachments() {
        when(fileAttachmentRepository.findAll()).thenReturn(List.of(fileAttachment));

        List<FileAttachment> attachments = fileAttachmentService.listFileAttachments();

        assertNotNull(attachments);
        assertFalse(attachments.isEmpty());
    }

    @Test
    void testGetFileAttachmentsByUserId_Success() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(fileAttachmentRepository.findByUserId(userId)).thenReturn(List.of(fileAttachment));

        List<FileAttachment> fileAttachments = fileAttachmentService.getFileAttachmentsByUserId(userId);

        assertNotNull(fileAttachments);
        assertEquals(userId, fileAttachments.get(0).getUser().getId());

        verify(fileAttachmentRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetFileAttachmentsByUserId_UserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> fileAttachmentService.getFileAttachmentsByUserId(userId));

        verify(fileAttachmentRepository, never()).findByUserId(any());
    }

    @Test
    void testGetFileAttachmentsByTaskId_Success() {
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(fileAttachmentRepository.findByTaskId(taskId)).thenReturn(List.of(fileAttachment));

        List<FileAttachment> fileAttachments = fileAttachmentService.getFileAttachmentsByTaskId(taskId);

        assertNotNull(fileAttachments);
        assertEquals(taskId, fileAttachments.get(0).getTask().getId());

        verify(fileAttachmentRepository, times(1)).findByTaskId(taskId);

    }

    @Test
    void testGetFileAttachmentsByTaskId_TaskNotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> fileAttachmentService.getFileAttachmentsByTaskId(taskId));

        verify(fileAttachmentRepository, never()).findByTaskId(any());
    }

}
