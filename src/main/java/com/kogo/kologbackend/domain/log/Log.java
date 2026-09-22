package com.kogo.kologbackend.domain.log;

import com.kogo.kologbackend.domains.user.infrastructure.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private UserJpaEntity user;

    @Builder
    public Log(String videoUrl, String caption, String date, Integer hour, UserJpaEntity user) {
        this.videoUrl = videoUrl;
        this.date = date;
        this.hour = hour;
        this.user = user;
        this.caption = caption;
    }

    public void updateCaption(String caption) {
        if (caption != null && !caption.isBlank()) {
            this.caption = caption;
        }
    }
}
