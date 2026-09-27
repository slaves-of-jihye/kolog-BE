package com.kogo.kologbackend.domains.comment.application.usecase.crud;

import com.kogo.kologbackend.domains.comment.application.exception.CommentLogNotFoundException;
import com.kogo.kologbackend.domains.comment.application.exception.CommentUserNotFoundException;
import com.kogo.kologbackend.domains.comment.application.exception.InvalidCommentContentException;
import com.kogo.kologbackend.domains.comment.application.external.CommentRepository;
import com.kogo.kologbackend.domains.comment.application.usecase.crud.dto.request.CommentCreateRequest;
import com.kogo.kologbackend.domains.comment.application.usecase.crud.dto.response.CommentResponse;
import com.kogo.kologbackend.domains.comment.domain.Comment;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCreateUseCase {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LogRepository logRepository;

    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new InvalidCommentContentException();
        }

        User author = userRepository.findById(request.requester().userId())
                .orElseThrow(CommentUserNotFoundException::new);
        Log log = logRepository.findById(request.logId())
                .orElseThrow(CommentLogNotFoundException::new);

        Comment saved = commentRepository.save(Comment.builder()
                .content(request.content())
                .logId(log.id())
                .author(author)
                .build());
        return CommentResponse.from(saved);
    }
}
