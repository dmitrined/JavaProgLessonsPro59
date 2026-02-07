package de.ait.javaproglessonspro.controllers;

import de.ait.javaproglessonspro.enums.ClientDocumentType;
import de.ait.javaproglessonspro.model.ClientDocumentDb;
import de.ait.javaproglessonspro.service.ClientDocumentDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientDocumentController {

    private final ClientDocumentDbService service;

    @PostMapping(value = "/documents/db", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientDocumentDb> uploadClientDocumentToDb(
            @RequestParam String clientEmail,
            @RequestParam ClientDocumentType docType,
            @RequestPart("file") MultipartFile file)
    {
        ClientDocumentDb saved = service.uploadClientDocument(clientEmail, docType, file);
        log.info("Client document with id {} saved", saved.getId());
        return  ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/documents/db/{documentId}/download")
    public ResponseEntity<byte[]> downloadClientDocument(@PathVariable Long documentId){
        ClientDocumentDb doc = service.getClientDocument(documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + doc.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(doc.getData());
    }

    @GetMapping("/documents/db")
    public List<ClientDocumentDb> listClientDocuments(@RequestParam String clientEmail){
        return service.getAllClientDocuments(clientEmail);
    }

}