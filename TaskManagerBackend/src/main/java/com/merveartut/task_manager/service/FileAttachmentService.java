package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.FileAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FileAttachmentService {
    FileAttachment createFileAttachment(UUID taskId, FileAttachment fileAttachment);

    FileAttachment uploadFile(MultipartFile file, UUID taskId, UUID userId) throws IOException;;

    List<FileAttachment> listFileAttachments();

    FileAttachment getFileAttachmentById(UUID id);

    List<FileAttachment> getFileAttachmentsByUserId(UUID userId);

    List<FileAttachment> getFileAttachmentsByTaskId(UUID taskId);

}
