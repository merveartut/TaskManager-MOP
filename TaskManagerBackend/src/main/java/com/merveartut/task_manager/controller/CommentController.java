package com.merveartut.task_manager.controller;

import com.merveartut.task_manager.model.Comment;
import com.merveartut.task_manager.service.CommentService;
import com.merveartut.task_manager.service.exception.CommentNotFoundException;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController (CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/v1/add-comment")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
       return ResponseEntity.ok(commentService.createComment(comment));
    }

    @GetMapping("/v1")
    public ResponseEntity<List<Comment>> getComments() {
        return ResponseEntity.ok(commentService.listComments());
    }

//    @GetMapping("/v1/{id}")
//    public ResponseEntity<Comment> getCommentById(@PathVariable UUID id) {
//        return ResponseEntity.ok(commentService.getCommentById(id));
//    }

    @GetMapping("/v1/task")
    public ResponseEntity<List<Comment>> getCommentsByTask(@RequestParam UUID taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException ex) {
        return ResponseEntity.status(404).body("Task not found");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex) {
        return  ResponseEntity.status(404).body("Project not found");
    }

}
