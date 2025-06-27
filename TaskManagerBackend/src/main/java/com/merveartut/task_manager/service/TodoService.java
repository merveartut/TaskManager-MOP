package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.Todo;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.TodoNotFoundException;

import java.util.List;
import java.util.UUID;

public interface TodoService {

    List<Todo> getTodosByTask(UUID taskId) throws TaskNotFoundException;
    Todo createTodo(Todo todo) throws TaskNotFoundException;
    Todo setCompletedState(UUID id, Boolean state) throws TodoNotFoundException;
}
