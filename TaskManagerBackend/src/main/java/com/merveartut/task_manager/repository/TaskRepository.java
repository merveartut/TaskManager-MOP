package com.merveartut.task_manager.repository;

import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    List<Task> findByProjectId(@Param("projectId")UUID projectId);

    @Query("SELECT t FROM Task t WHERE t.state = :state")
    List<Task> findByState(@Param("state")TaskState state);

    @Query("SELECT t FROM Task t WHERE t.priority = :priority")
    List<Task> findByPriority(@Param("priority")TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId")
    List<Task> findByAssigneeId(@Param("assigneeId")UUID assigneeId);

}
