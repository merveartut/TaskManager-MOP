package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.Todo;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.TodoRepository;
import com.merveartut.task_manager.service.exception.CommentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.TodoNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;
    private final TaskRepository taskRepository;

    public TodoServiceImpl(TodoRepository todoRepository, TaskRepository taskRepository) {
        this.todoRepository = todoRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Todo> getTodosByTask(UUID taskId) throws TodoNotFoundException {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException();
        }
        return todoRepository.findByTaskId(taskId);
    }

    @Override
    public Todo createTodo(Todo todo) throws TaskNotFoundException {
        Optional<Task> task = taskRepository.findById(todo.getTask().getId());
        if (task.isPresent()) {
            todo.setTask(task.get());
            return todoRepository.save(todo);
        } else {
            throw new TodoNotFoundException();
        }
    }


    @Override
    public Todo setCompletedState(UUID id, Boolean state) throws TodoNotFoundException {
        Optional<Todo> todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            todo.get().setCompletedState(state);
            return todoRepository.save(todo.get());
        } else {
            throw new TodoNotFoundException();
        }
    }

}
