package de.ait.javaproglessonspro.service;

import de.ait.javaproglessonspro.enums.CandidateDocType;
import de.ait.javaproglessonspro.model.CandidateDocumentOs;
import de.ait.javaproglessonspro.repository.CandidateDocumentOsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateDocumentOsService {

    private final CandidateDocumentOsRepository repository;

    @Value("${app.upload.candidate-docs-dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private static final int MAX_DOCUMENTS_PER_CANDIDATE = 10;

    public CandidateDocumentOs uploadDocument(String email, CandidateDocType docType, MultipartFile file) {
        validateUpload(email, docType, file);

        long count = repository.findAllByCandidateEmail(email).size();
        if (count >= MAX_DOCUMENTS_PER_CANDIDATE) {
            log.warn("Rejected candidate upload: email={}, docType={}, filename={}, reason={}",
                    email, docType, file.getOriginalFilename(), "Maximum documents reached (10)");
            throw new IllegalArgumentException("Maximum documents reached (10)");
        }

        String normalizedEmail = email.replace("@", "_at_").replace(".", "_");
        Path targetFolder = Paths.get(uploadDir, normalizedEmail, docType.name());

        try {
            Files.createDirectories(targetFolder);

            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetPath = targetFolder.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            CandidateDocumentOs metadata = CandidateDocumentOs.builder()
                    .candidateEmail(email)
                    .docType(docType)
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .storagePath(targetPath.toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            return repository.save(metadata);

        } catch (IOException e) {
            log.error("Failed to store file: email={}, docType={}, filename={}", email, docType, file.getOriginalFilename(), e);
            throw new RuntimeException("Could not store file", e);
        }
    }

    private void validateUpload(String email, CandidateDocType docType, MultipartFile file) {
        String reason = null;

        if (email == null || !email.contains("@")) {
            reason = "Invalid email";
        } else if (file == null || file.isEmpty()) {
            reason = "File is empty";
        } else if (file.getSize() > MAX_FILE_SIZE) {
            reason = "File size exceeds 5MB";
        } else if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            reason = "Unsupported content type: " + file.getContentType();
        }

        if (reason != null) {
            log.warn("Rejected candidate upload: email={}, docType={}, filename={}, reason={}",
                    email, docType, file != null ? file.getOriginalFilename() : "null", reason);
            throw new IllegalArgumentException(reason);
        }
    }

    public List<CandidateDocumentOs> listDocuments(String email, CandidateDocType docType) {
        List<CandidateDocumentOs> docs = repository.findAllByCandidateEmail(email);
        if (docType != null) {
            return docs.stream()
                    .filter(d -> d.getDocType() == docType)
                    .toList();
        }
        return docs;
    }

    public CandidateDocumentOs getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    }

    @Transactional
    public void deleteDocument(Long id) {
        CandidateDocumentOs doc = getById(id);
        try {
            Files.deleteIfExists(Paths.get(doc.getStoragePath()));
        } catch (IOException e) {
            log.error("Failed to delete file from disk: {}", doc.getStoragePath(), e);
        }
        repository.delete(doc);
    }

    @Transactional
    public void deleteAllByEmail(String email) {
        List<CandidateDocumentOs> docs = repository.findAllByCandidateEmail(email);
        for (CandidateDocumentOs doc : docs) {
            try {
                Files.deleteIfExists(Paths.get(doc.getStoragePath()));
            } catch (IOException e) {
                log.error("Failed to delete file from disk: {}", doc.getStoragePath(), e);
            }
        }
        repository.deleteAllByCandidateEmail(email);
    }
}
