package de.ait.javaproglessonspro.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ait.javaproglessonspro.enums.CandidateDocType;
import de.ait.javaproglessonspro.model.CandidateDocumentOs;
import de.ait.javaproglessonspro.repository.CandidateDocumentOsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CandidateDocumentOsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateDocumentOsRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.upload.candidate-docs-dir}")
    private String uploadDir;

    @BeforeEach
    void setUp() throws Exception {
        repository.deleteAll();
        Path dir = Paths.get(uploadDir);
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (Exception ignored) {}
                    });
        }
        Files.createDirectories(dir);
    }

    @Test
    void testUploadShouldSaveFileAndMetadata() throws Exception {
        String email = "anna@mail.com";
        byte[] content = "my resume content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", content);

        String response = mockMvc.perform(multipart("/api/candidates/documents/os")
                        .file(file)
                        .param("candidateEmail", email)
                        .param("docType", CandidateDocType.CV.name())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CandidateDocumentOs saved = objectMapper.readValue(response, CandidateDocumentOs.class);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCandidateEmail()).isEqualTo(email);
        assertThat(saved.getOriginalFilename()).isEqualTo("resume.pdf");

        // Verify DB
        List<CandidateDocumentOs> all = repository.findAllByCandidateEmail(email);
        assertThat(all).hasSize(1);

        // Verify FS
        Path storedPath = Paths.get(saved.getStoragePath());
        assertThat(Files.exists(storedPath)).isTrue();
        assertThat(Files.readAllBytes(storedPath)).isEqualTo(content);
        assertThat(storedPath.toString()).contains("anna_at_mail_com", "CV");
    }

    @Test
    void testUpload_ShouldRejectWhenMaxDocumentsReached() throws Exception {
        String email = "busy@mail.com";
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", "content".getBytes());

        // Upload 10 documents
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(multipart("/api/candidates/documents/os")
                            .file(file)
                            .param("candidateEmail", email)
                            .param("docType", CandidateDocType.CV.name()))
                    .andExpect(status().isCreated());
        }

        // 11th should fail
        mockMvc.perform(multipart("/api/candidates/documents/os")
                        .file(file)
                        .param("candidateEmail", email)
                        .param("docType", CandidateDocType.CV.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFullCycle_Upload_List_Download_Delete() throws Exception {
        String email = "test@example.com";
        byte[] content = "test data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "cert.png", "image/png", content);

        // 1. Upload
        String uploadResp = mockMvc.perform(multipart("/api/candidates/documents/os")
                        .file(file)
                        .param("candidateEmail", email)
                        .param("docType", CandidateDocType.CERTIFICATE.name()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long docId = objectMapper.readTree(uploadResp).get("id").asLong();

        // 2. List
        mockMvc.perform(get("/api/candidates/documents/os")
                        .param("candidateEmail", email))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
                    assertThat(node.isArray()).isTrue();
                    assertThat(node.size()).isEqualTo(1);
                    assertThat(node.get(0).get("id").asLong()).isEqualTo(docId);
                });

        // 3. Download
        byte[] downloadedBytes = mockMvc.perform(get("/api/candidates/documents/os/{id}/download", docId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        assertThat(downloadedBytes).isEqualTo(content);

        // 4. Delete
        mockMvc.perform(delete("/api/candidates/documents/os/{id}", docId))
                .andExpect(status().isNoContent());

        // 5. Verify deletion
        assertThat(repository.existsById(docId)).isFalse();
        CandidateDocumentOs doc = objectMapper.readValue(uploadResp, CandidateDocumentOs.class);
        assertThat(Files.exists(Paths.get(doc.getStoragePath()))).isFalse();
    }

    @Test
    void testDeleteAllByEmail() throws Exception {
        String email = "todelete@mail.com";
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", "content".getBytes());

        mockMvc.perform(multipart("/api/candidates/documents/os")
                        .file(file)
                        .param("candidateEmail", email)
                        .param("docType", CandidateDocType.CV.name()))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/candidates/documents/os")
                        .param("candidateEmail", email))
                .andExpect(status().isNoContent());

        assertThat(repository.findAllByCandidateEmail(email)).isEmpty();
    }
}
