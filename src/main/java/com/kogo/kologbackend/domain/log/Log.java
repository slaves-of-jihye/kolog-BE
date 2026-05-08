package com.kogo.kologbackend.domain.log;

import com.kogo.kologbackend.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private String videoUrl;

    private String caption;

    private String date;

    private Integer hour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Log(String videoUrl, String caption, String date, Integer hour, User user) {
        this.videoUrl = videoUrl;
        this.date = date;
        this.hour = hour;
        this.user = user;
        this.caption = caption;
    }
}
