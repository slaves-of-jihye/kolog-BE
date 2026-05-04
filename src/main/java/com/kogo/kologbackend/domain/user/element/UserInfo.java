package com.kogo.kologbackend.domain.user.element;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInfo {
    private String nickname;
    private String profileImage;
}
