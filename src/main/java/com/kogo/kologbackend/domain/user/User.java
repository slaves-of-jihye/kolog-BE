package com.kogo.kologbackend.domain.user;

import com.kogo.kologbackend.domain.user.element.UserInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Embedded
    @Setter
    private UserInfo userInfo;

    @Builder
    public User(String email) {
        this.email = email;
    }
}
