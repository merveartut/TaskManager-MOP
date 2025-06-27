package com.merveartut.task_manager.controller;

import com.merveartut.task_manager.model.Todo;
import com.merveartut.task_manager.service.TodoService;
import com.merveartut.task_manager.service.exception.TodoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/v1")
    public ResponseEntity<List<Todo>> getTodosByTask (@RequestParam UUID taskId) {
        return ResponseEntity.ok(todoService.getTodosByTask(taskId));
    }

    @PostMapping("/v1")
    public ResponseEntity<Todo> createTodo (@RequestBody Todo todo) {
        return ResponseEntity.ok(todoService.createTodo(todo));
    }

    @PutMapping("/v1/update-state")
    public ResponseEntity<Todo> setCompletedState (@RequestParam UUID id, @RequestParam Boolean state) {
        return ResponseEntity.ok(todoService.setCompletedState(id, state));
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<String> handleTodoNotFoundException(TodoNotFoundException ex) {
        return ResponseEntity.status(404).body("Todo not found");
    }
}
