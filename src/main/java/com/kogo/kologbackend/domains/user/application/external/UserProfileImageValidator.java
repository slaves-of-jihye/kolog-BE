package com.kogo.kologbackend.domains.user.application.external;

import java.io.BufferedInputStream;
import java.io.IOException;

public interface UserProfileImageValidator {
    String detectSupportedMediaType(BufferedInputStream image) throws IOException;
}
