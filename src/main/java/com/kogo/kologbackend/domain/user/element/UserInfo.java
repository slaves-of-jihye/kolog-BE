package com.kogo.kologbackend.domain.user.element;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String nickname;
    private String profileImage;
}
