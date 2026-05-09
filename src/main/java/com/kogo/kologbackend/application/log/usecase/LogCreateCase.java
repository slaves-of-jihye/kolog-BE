package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.request.LogCreateRequest;
import com.kogo.kologbackend.application.log.dto.response.LogCreateResponse;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogCreateUseCase;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.log.Log;
import com.kogo.kologbackend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogCreateCase implements LogCreateUseCase {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Override
    public LogCreateResponse logCreate(Long userId, LogCreateRequest logCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        boolean exist = logRepository.existsByUserIdAndDateAndHour(userId, logCreateRequest.date(), logCreateRequest.hour());
        if(exist) {
            throw new RuntimeException("한 시간에 하나의 로그만 올릴 수 있습니다.");
        }

        String videoUrl = "https:// ... 나중에 추가될 스토리지 주소" + logCreateRequest.videoFile().getOriginalFilename();

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
