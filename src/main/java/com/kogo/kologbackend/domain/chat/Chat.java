package com.kogo.kologbackend.domain.chat;

import com.kogo.kologbackend.domain.log.Log;
import com.kogo.kologbackend.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "log_id")
    private Log log;

    private String chatContent;

    @Builder
    public Chat(User user, Log log,String chatContent) {
        this.user = user;
        this.log = log;
        this.chatContent = chatContent;
    }
}
