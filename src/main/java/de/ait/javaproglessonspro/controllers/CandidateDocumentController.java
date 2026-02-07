package de.ait.javaproglessonspro.controllers;

import de.ait.javaproglessonspro.enums.CandidateDocType;
import de.ait.javaproglessonspro.model.CandidateDocumentOs;
import de.ait.javaproglessonspro.service.CandidateDocumentOsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/candidates/documents/os")
@RequiredArgsConstructor
public class CandidateDocumentController {

    private final CandidateDocumentOsService service;

    @Tag(name = "Candidate document management API")
    @Operation(summary = "Upload a document for a candidate")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidateDocumentOs> uploadDocument(
            @RequestParam String candidateEmail,
            @RequestParam CandidateDocType docType,
            @RequestPart("file") MultipartFile file) {
        CandidateDocumentOs saved = service.uploadDocument(candidateEmail, docType, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Tag(name = "Candidate document management API")
    @Operation(summary = "List all documents for a candidate")
    @GetMapping
    public ResponseEntity<List<CandidateDocumentOs>> listDocuments(
            @RequestParam String candidateEmail,
            @RequestParam(required = false) CandidateDocType docType) {
        return ResponseEntity.ok(service.listDocuments(candidateEmail, docType));
    }

    @Tag(name = "Candidate document management API")
    @Operation(summary = "Download a document for a candidate")
    @GetMapping("/{documentId}/download")
    public ResponseEntity<FileSystemResource> downloadDocument(@PathVariable Long documentId) {
        CandidateDocumentOs doc = service.getById(documentId);
        FileSystemResource resource = new FileSystemResource(doc.getStoragePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(resource);
    }

    @Tag(name = "Candidate document management API")
    @Operation(summary = "Delete a document for a candidate")
    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable Long documentId) {
        service.deleteDocument(documentId);
    }

    @Tag(name = "Candidate document management API")
    @Operation(summary = "Delete all documents for a candidate")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByEmail(@RequestParam String candidateEmail) {
        service.deleteAllByEmail(candidateEmail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
