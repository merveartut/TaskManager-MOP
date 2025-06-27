package com.merveartut.task_manager.repository;

import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository  extends JpaRepository<Project, UUID> {
    @Query("SELECT p FROM Project p WHERE p.projectManager.id = :userId")
    List<Project> findByManagerId(@Param("userId")UUID userId);

    @Query("SELECT p FROM Project p WHERE :user MEMBER OF p.teamMembers OR p.projectManager = :user")
    List<Project> findAllByTeamMemberOrManager(@Param("user") User user);

}
