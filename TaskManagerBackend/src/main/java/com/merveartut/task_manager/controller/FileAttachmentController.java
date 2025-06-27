package com.merveartut.task_manager.controller;

import com.merveartut.task_manager.model.FileAttachment;
import com.merveartut.task_manager.service.FileAttachmentService;
import com.merveartut.task_manager.service.exception.FileAttachmentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
public class FileAttachmentController {

    private final FileAttachmentService fileAttachmentService;

    @Autowired
    public FileAttachmentController(FileAttachmentService fileAttachmentService) {
        this.fileAttachmentService = fileAttachmentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileAttachment> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam UUID taskId,
            @RequestParam UUID userId) throws IOException {

        FileAttachment savedAttachment = fileAttachmentService.uploadFile(file, taskId, userId);
        return ResponseEntity.ok(savedAttachment);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Path path = Paths.get("uploads").resolve(filename).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found");
        }

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resource);
    }

    @PostMapping("/v1")
    public ResponseEntity<FileAttachment> createFileAttachment(@RequestParam UUID taskId, @RequestBody FileAttachment fileAttachment) {
        return ResponseEntity.ok(fileAttachmentService.createFileAttachment(taskId, fileAttachment));
    }

    @GetMapping("/v1")
    public ResponseEntity<List<FileAttachment>> getFileAttachments() {
        return ResponseEntity.ok(fileAttachmentService.listFileAttachments());
    }

    @GetMapping("/v1/{id}")
    public ResponseEntity<FileAttachment> getFileAttachmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(fileAttachmentService.getFileAttachmentById(id));
    }

    @GetMapping("/v1/task")
    public ResponseEntity<List<FileAttachment>> getFileAttachmentsByTaskId(@RequestParam UUID taskId) {
        return ResponseEntity.ok(fileAttachmentService.getFileAttachmentsByTaskId(taskId));
    }

    @ExceptionHandler(FileAttachmentNotFoundException.class)
    public ResponseEntity<String> handleFileAttachmentNotFoundException(FileAttachmentNotFoundException ex) {
        return ResponseEntity.status(404).body("Project not found");
    }
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException ex) {
        return ResponseEntity.status(404).body("Project not found");
    }
}
