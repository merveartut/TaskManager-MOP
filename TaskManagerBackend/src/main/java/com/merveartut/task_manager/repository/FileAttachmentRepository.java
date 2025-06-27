package com.merveartut.task_manager.repository;

import com.merveartut.task_manager.model.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID> {

    @Query("SELECT f FROM FileAttachment f WHERE f.user.id = :userId")
    List<FileAttachment> findByUserId(@Param("userId")UUID userId);

    @Query("SELECT f FROM FileAttachment f WHERE f.task.id = :taskId")
    List<FileAttachment> findByTaskId(@Param("taskId")UUID taskId);
}
