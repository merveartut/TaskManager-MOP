package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.Comment;
import com.merveartut.task_manager.service.exception.CommentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment createComment(Comment comment);

    List<Comment> listComments();

    Comment getCommentById(UUID id) throws CommentNotFoundException;

    List<Comment> getCommentsByTask(UUID taskId) throws TaskNotFoundException;

    Comment updateComment(UUID id, Comment comment) throws CommentNotFoundException;

}
