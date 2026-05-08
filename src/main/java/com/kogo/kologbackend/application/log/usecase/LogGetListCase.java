package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogGetListUseCase;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.log.Log;
import com.kogo.kologbackend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LogGetListCase implements LogGetListUseCase {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Override
    public List<LogGetListResponse> list(String date) {
        List<Log> byDate = logRepository.findByDate(date);

        List<Long> userIds = byDate.stream()
                .map(log -> log.getUser().getId())
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<String, List<Log>> groupedByDate = byDate.stream()
                .collect(Collectors.groupingBy(Log::getDate));

        return groupedByDate.values().stream()
                .map(logsInDate -> {
                    Collections.shuffle(logsInDate);
                    return logsInDate.get(0);
                })
                .map(log -> {
                    User user = userMap.get(log.getUser().getId());
                    return new LogGetListResponse(
                            log.getDate(),
                            log.getHour(),
                            log.getUser().getId(),
                            user != null ? user.getUserInfo().getNickname() : "Unknown", // 유저 이름 추가
                            user != null ? user.getUserInfo().getProfileImage() : null, // 유저 이미지 추가
                            log.getVideoUrl(),
                            log.getCaption()
                    );
                })
                .toList();
    }


}
