package com.merveartut.task_manager.controller;

import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.service.TaskService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/v1")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('GUEST')")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        System.out.println(task);
       return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping("/v1")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('GUEST')")
    public ResponseEntity<List<Task>> getTasks () {
        return ResponseEntity.ok(taskService.listTasks());
    }

    @GetMapping("/v1/task/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('TEAM_MEMBER') or hasRole('GUEST')")
    public ResponseEntity<Task> getTaskById (@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/v1/project/{projectId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('GUEST')")
    public List<Task> getTasksByProjectId(@PathVariable UUID projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    @GetMapping("/v1/user/{userId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('TEAM_MEMBER') or hasRole('GUEST')")
    public List<Task> getTasksByUserId(@PathVariable UUID userId) {
        return taskService.getTasksByAssignee(userId);
    }

//    @GetMapping
//    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('TEAM_MEMBER')")
//    public ResponseEntity<TaskState> getTaskState (@PathVariable UUID id) {
//        return ResponseEntity.ok(taskService.getTaskState(id));
//    }

    @PutMapping("/v1/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('GUEST')")
    public ResponseEntity<Task> updateTask (@RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(task));
    }


    @PutMapping("/v1/set-assignee")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('GUEST')")
    public ResponseEntity<Task> setTaskAssignee (@RequestParam UUID id, @RequestParam UUID userId) {
        return ResponseEntity.ok(taskService.setTaskAssignee(id, userId));
    }

    @PutMapping("/v1/set-priority")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('GUEST')")
    public ResponseEntity<Task> setTaskPriority (@RequestParam UUID id, @RequestParam TaskPriority priority) {
        return ResponseEntity.ok(taskService.setTaskPriority(id, priority));
    }

    @PutMapping("/v1/set-state")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('TEAM_MEMBER') or hasRole('GUEST')")
    public ResponseEntity<Task> setTaskState (@RequestParam UUID id, @RequestParam TaskState state, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(taskService.setTaskState(id, state, reason));
    }

    @DeleteMapping("/v1")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER') or hasRole('GUEST')")
    public ResponseEntity<Void> deleteTask (@RequestParam UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException ex) {
        return ResponseEntity.status(404).body("Task not found");
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException ex) {
        return  ResponseEntity.status(404).body("Project not found");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return  ResponseEntity.status(404).body("User not found");
    }

}
