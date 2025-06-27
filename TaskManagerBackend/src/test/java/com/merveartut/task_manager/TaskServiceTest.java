package com.merveartut.task_manager;

import com.merveartut.task_manager.enums.ProjectStatus;
import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.TaskServiceImpl;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskServiceTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    private Task task;
    private UUID taskId;
    private User user;
    private UUID userId;
    private Project project;
    private UUID projectId;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        project = new Project();
        project.setId(projectId);
        project.setProjectManager(user);

        task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("This is a test task.")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.LOW)
                .assignee(user)
                .project(project)
                .build();
    }

    @Test
    void createTask_Success() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("username", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        authentication.setDetails(project.getProjectManager().getId().toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        assertEquals(TaskState.BACKLOG, createdTask.getState());
        assertEquals(TaskPriority.LOW, createdTask.getPriority());
        assertEquals(user, createdTask.getAssignee());
        assertEquals(project, createdTask.getProject());

        verify(taskRepository, times(1)).save(task);
    }
    @Test
    void createTask_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> taskService.createTask(task));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_UserNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.createTask(task));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void listTasks_ReturnsTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.listTasks();

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(task.getTitle(), tasks.get(0).getTitle());

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(taskId);

        assertNotNull(foundTask);
        assertEquals(taskId, foundTask.getId());

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_NotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));
    }

    @Test
    void getTaskByProjectId_Success() {
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(taskRepository.findByProjectId(projectId)).thenReturn(List.of(task));

        List<Task> tasks = taskService.getTasksByProjectId(projectId);

        assertNotNull(tasks);
        assertEquals(projectId, tasks.get(0).getProject().getId());
    }

    @Test
    void getTaskByProjectId_ProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> taskService.getTasksByProjectId(projectId));

        verify(taskRepository, never()).findByProjectId(any());
    }

    @Test
    void getTaskState_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskState result = taskService.getTaskState(taskId);
        assertEquals(TaskState.BACKLOG, result);

        verify(taskRepository, times(1)).findById(taskId);
    }
    @Test
    void getTaskByState_Success() {
        when(taskRepository.findByState(TaskState.BACKLOG)).thenReturn(List.of(task));

        List<Task> tasks = taskService.getTasksByState(TaskState.BACKLOG);

        assertEquals(TaskState.BACKLOG, tasks.get(0).getState());

        verify(taskRepository, times(1)).findByState(TaskState.BACKLOG);
    }

    @Test
    void getTasksByPriority_Success() {
        when(taskRepository.findByPriority(TaskPriority.LOW)).thenReturn(List.of(task));

        List<Task> tasks = taskService.getTasksByPriority(TaskPriority.LOW);

        assertEquals(TaskPriority.LOW, tasks.get(0).getPriority());

        verify(taskRepository, times(1)).findByPriority(TaskPriority.LOW);
    }

    @Test
    void getTaskByAssignee_Success() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(taskRepository.findByAssigneeId(userId)).thenReturn(List.of(task));

        List<Task> tasks = taskService.getTasksByAssignee(userId);

        assertNotNull(tasks);
        assertEquals(userId, tasks.get(0).getAssignee().getId());
    }

    @Test
    void getTaskByAssignee_UserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> taskService.getTasksByAssignee(userId));

        verify(taskRepository, never()).findByAssigneeId(any());
    }

    @Test
    void updateTask_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.updateTask(task);

        assertNotNull(updatedTask);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_NotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(task));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void setTaskState_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.setTaskState(taskId, TaskState.IN_ANALYSIS, null);

        assertEquals(TaskState.IN_ANALYSIS, updatedTask.getState());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void setTaskState_blockedWithoutReason_ThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> taskService.setTaskState(taskId, TaskState.BLOCKED, null));
    }

    @Test
    void setTaskState_cancelledWithoutReason_ThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> taskService.setTaskState(taskId, TaskState.CANCELLED, null));
    }

    @Test
    void setTaskState_Completed_ThrowsException() {
        task.setState(TaskState.COMPLETED);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class, () -> taskService.setTaskState(taskId, TaskState.IN_DEVELOPMENT, null));
    }

    @Test
    void setTaskPriority_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.setTaskPriority(taskId, TaskPriority.MEDIUM);

            assertEquals(TaskPriority.MEDIUM, updatedTask.getPriority());
        verify(taskRepository, times(1)).save(task);
    }
    @Test
    void setTaskPriority_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.setTaskPriority(taskId, TaskPriority.MEDIUM));
    }

    @Test
    void testSetTaskAssignee_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.setTaskAssignee(taskId, userId);

        assertNotNull(updatedTask);
        assertEquals(userId, updatedTask.getAssignee().getId());

        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testSetTaskAssignee_TaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.setTaskAssignee(taskId, userId));

        verify(taskRepository, times(1)).findById(taskId);
        verifyNoInteractions(userRepository);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testDeleteTask_TaskNotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, never()).deleteById(any());
    }
    }
