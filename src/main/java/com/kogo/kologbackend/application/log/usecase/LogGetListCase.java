package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogGetListUseCase;
import com.kogo.kologbackend.domain.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class LogGetListCase implements LogGetListUseCase {

    private final LogRepository logRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LogGetListResponse> list(String date) {
        return logRepository.findByDate(date).stream()
                .map(log -> {
                    return new LogGetListResponse(
                            log.getDate(),
                            log.getHour(),
                            log.getUser().getId(),
                            log.getUser().getUserInfo().getNickname(),
                            log.getUser().getUserInfo().getProfileImage(),
                            log.getVideoUrl(),
                            log.getCaption()
                    );
                })
                .toList();
    }


}
