package com.kogo.kologbackend.domains.log.application.log.usecase;

import com.kogo.kologbackend.domains.log.application.log.dto.request.LogCreateRequest;
import com.kogo.kologbackend.domains.log.application.log.dto.response.LogCreateResponse;
import com.kogo.kologbackend.domains.log.application.log.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.log.internal.LogCreateUseCase;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaRepository;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaEntity;
import com.kogo.kologbackend.global.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogCreateCase implements LogCreateUseCase {

    private final LogRepository logRepository;
    private final UserJpaRepository userRepository;
    private final FileService fileService;

    @Override
    @Transactional
    public LogCreateResponse logCreate(Long userId, LogCreateRequest logCreateRequest) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        boolean exist = logRepository.existsByUserIdAndDateAndHour(userId, logCreateRequest.date(), logCreateRequest.hour());
        if(exist) {
            throw new RuntimeException("한 시간에 하나의 로그만 올릴 수 있습니다.");
        }

        String videoUrl = fileService.storeVideo(logCreateRequest.videoFile());

        Log log = Log.builder()
                .videoUrl(videoUrl)
                .caption(logCreateRequest.caption())
                .date(logCreateRequest.date())
                .hour(logCreateRequest.hour())
                .user(user)
                .build();

        Log saveLog = logRepository.save(log);

        return new LogCreateResponse(
                user.getId(),
                saveLog.getLogId(),
                saveLog.getVideoUrl(),
                saveLog.getCaption(),
                saveLog.getHour(),
                saveLog.getDate()
        );
    }
}
