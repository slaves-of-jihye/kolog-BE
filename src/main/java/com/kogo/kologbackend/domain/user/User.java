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

    @Column(nullable = false)
    private String password;

    @Embedded
    @Setter
    private UserInfo userInfo = new UserInfo();

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.userInfo.setNickname(nickname);
    }

    public void updateProfile(String nickname, String profileImage) {
        this.userInfo.setNickname(nickname);
        this.userInfo.setProfileImage(profileImage);
    }
}
