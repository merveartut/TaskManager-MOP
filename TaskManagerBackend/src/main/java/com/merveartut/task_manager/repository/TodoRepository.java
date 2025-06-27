package com.merveartut.task_manager.repository;

import com.merveartut.task_manager.model.FileAttachment;
import com.merveartut.task_manager.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    @Query("SELECT t FROM Todo t WHERE t.task.id = :taskId")
    List<Todo> findByTaskId(@Param("taskId")UUID taskId);
}
