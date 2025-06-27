package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Comment;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.CommentRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.exception.CommentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Comment createComment(Comment comment) {
      Task task = taskRepository.findById(comment.getTask().getId())
              .orElseThrow(() -> new TaskNotFoundException());
      System.out.println(comment.getCommenter());
      if (comment.getCommenter().getRole() == Role.GUEST ) {
          comment.setTask(task);
      } else {
          User commenter = userRepository.findById(comment.getCommenter().getId())
                  .orElseThrow(() -> new UserNotFoundException());
          comment.setTask(task);
          comment.setCommenter(commenter);
      }
        return commentRepository.save(comment);

    }

    @Override
    public List<Comment> listComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getCommentById(UUID id) throws CommentNotFoundException {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException());
    }

    @Override
    public List<Comment> getCommentsByTask(UUID taskId) throws TaskNotFoundException {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException();
        }
        return commentRepository.findByTaskId(taskId);
    }

    @Override
    public Comment updateComment(UUID id, Comment comment) throws CommentNotFoundException {
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException();
        }
        return commentRepository.save(comment);
    }
}