package com.kogo.kologbackend.domains.log.infrastructure.adapter;

import com.kogo.kologbackend.domains.comment.domain.Comment;
import com.kogo.kologbackend.domains.comment.infrastructure.jpa.CommentJpaRepository;
import com.kogo.kologbackend.domains.comment.infrastructure.jpa.CommentMapper;
import com.kogo.kologbackend.domains.emotion.domain.Emotion;
import com.kogo.kologbackend.domains.emotion.infrastructure.jpa.EmotionJpaRepository;
import com.kogo.kologbackend.domains.emotion.infrastructure.jpa.EmotionMapper;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogJpaRepository;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogMapper;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LogRepositoryAdapter implements LogRepository {
    private final LogJpaRepository logs;
    private final UserJpaRepository users;
    private final CommentJpaRepository comments;
    private final EmotionJpaRepository emotions;

    @Override
    public Log save(Log log) {
        var user = users.getReferenceById(log.uploader().id());
        var saved = logs.save(LogMapper.toEntity(log, user));
        return LogMapper.toDomain(saved, findComments(saved.getId()), findEmotions(saved.getId()));
    }

    @Override
    public Optional<Log> findById(Long id) {
        return logs.findById(id)
                .map(entity -> LogMapper.toDomain(entity, findComments(entity.getId()), findEmotions(entity.getId())));
    }

    @Override
    public List<Log> findByDateAndHour(LocalDate date, Integer hour) {
        return logs.findByDateAndHour(date, hour).stream()
                .map(entity -> LogMapper.toDomain(entity, findComments(entity.getId()), findEmotions(entity.getId())))
                .toList();
    }

    @Override
    public List<Log> findByDate(LocalDate date) {
        return logs.findByDate(date).stream()
                .map(entity -> LogMapper.toDomain(entity, findComments(entity.getId()), findEmotions(entity.getId())))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        comments.deleteByLog_Id(id);
        emotions.deleteByLog_Id(id);
        logs.deleteById(id);
    }

    private List<Comment> findComments(Long logId) {
        return comments.findByLog_IdOrderByIdAsc(logId).stream()
                .map(CommentMapper::toDomain)
                .toList();
    }

    private List<Emotion> findEmotions(Long logId) {
        return emotions.findByLog_IdOrderByIdAsc(logId).stream()
                .map(EmotionMapper::toDomain)
                .toList();
    }
}
