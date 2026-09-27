package com.kogo.kologbackend.domains.emotion.infrastructure.jpa;

import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emotions", uniqueConstraints = @UniqueConstraint(columnNames = {"log_id", "user_id"}))
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class EmotionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    private LogJpaEntity log;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserJpaEntity author;
}
