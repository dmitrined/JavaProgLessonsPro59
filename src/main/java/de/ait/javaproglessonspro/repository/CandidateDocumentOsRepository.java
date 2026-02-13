package de.ait.javaproglessonspro.repository;

import de.ait.javaproglessonspro.model.CandidateDocumentOs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateDocumentOsRepository extends JpaRepository<CandidateDocumentOs, Long> {

    List<CandidateDocumentOs> findAllByCandidateEmail(String email);

    void deleteAllByCandidateEmail(String email);
}
