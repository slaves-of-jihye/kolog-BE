package com.kogo.kologbackend.domains.log.application.external;

import java.io.BufferedInputStream;
import java.io.IOException;

public interface LogVideoValidator {
    String detectSupportedMediaType(BufferedInputStream video) throws IOException;
}
