package org.exception.demo.application.repository;

import java.util.Optional;

import org.exception.demo.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	Page<Document> findByUserId(Integer userId, Pageable pageable);

	Optional<Document> findByIdAndUserId(Long documentId, Integer userId);

}
