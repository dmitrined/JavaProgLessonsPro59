package de.ait.javaproglessonspro.model;

import de.ait.javaproglessonspro.enums.CandidateDocType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_documents_os")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDocumentOs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "candidate_email", nullable = false)
    private String candidateEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private CandidateDocType docType;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
