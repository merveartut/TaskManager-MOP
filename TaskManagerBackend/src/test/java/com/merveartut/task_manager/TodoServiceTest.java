package com.merveartut.task_manager;

import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.Todo;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.TodoRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.TaskServiceImpl;
import com.merveartut.task_manager.service.TodoServiceImpl;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.TodoNotFoundException;
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

public class TodoServiceTest {

    @InjectMocks
    private TodoServiceImpl todoService;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    private Todo todo;
    private UUID todoId;
    private Task task;
    private UUID taskId;
    private User user;
    private UUID userId;
    private Project project;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        todoId = UUID.randomUUID();
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
        todo = Todo.builder()
                .id(todoId)
                .text("Resolve bugs")
                .completedState(false)
                .task(task)
                .build();
    }

    @Test
    void createTodo_Success() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("username", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        authentication.setDetails(project.getProjectManager().getId().toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(todoRepository.save(todo)).thenReturn(todo);

        Todo createdTodo = todoService.createTodo(todo);

        assertNotNull(createdTodo);
        assertEquals("Resolve bugs", todo.getText());
        assertEquals(false, todo.getCompletedState());
        assertEquals(task, todo.getTask());

        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void getTodosByTask_Success() {
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(todoRepository.findByTaskId(taskId)).thenReturn(List.of(todo));

        List<Todo> todos = todoService.getTodosByTask(taskId);

        assertNotNull(todos);
        assertEquals(taskId, todos.get(0).getTask().getId());
    }

    @Test
    void getTodosByTask_TaskNotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> todoService.getTodosByTask(taskId));

        verify(todoRepository, never()).findByTaskId(any());
    }

    @Test
    void setCompletedState_Success() {
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);

        Todo updatedTodo = todoService.setCompletedState(todoId, true);

        assertEquals(true, updatedTodo.getCompletedState());
        verify(todoRepository, times(1)).save(todo);
    }
    @Test
    void setCompletedState_TodoNotFound() {
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.setCompletedState(todoId, true));
    }
}
