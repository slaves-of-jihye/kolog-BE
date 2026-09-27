package com.kogo.kologbackend.domains.comment.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, Long> {
    List<CommentJpaEntity> findByLog_IdOrderByIdAsc(Long logId);
    void deleteByLog_Id(Long logId);
}
