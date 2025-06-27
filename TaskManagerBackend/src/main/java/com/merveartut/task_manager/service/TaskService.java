package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task createTask(Task task);

    List<Task> listTasks();

    Task getTaskById(UUID id) throws TaskNotFoundException;

    TaskState getTaskState(UUID id) throws TaskNotFoundException;

    List<Task> getTasksByProjectId(UUID projectId);

    List<Task> getTasksByState(TaskState state);

    List<Task> getTasksByPriority(TaskPriority priority);

    List<Task> getTasksByAssignee(UUID userId);

    Task updateTask(Task task) throws TaskNotFoundException;

    Task setTaskState(UUID id, TaskState state, String reason) throws TaskNotFoundException;

    Task setTaskAssignee(UUID id, UUID userId) throws TaskNotFoundException, UserNotFoundException;

    Task setTaskPriority(UUID id, TaskPriority priority) throws TaskNotFoundException;

    void deleteTask(UUID id) throws TaskNotFoundException;
}
