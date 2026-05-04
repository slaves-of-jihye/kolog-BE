package com.kogo.kologbackend.adapter.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleLoginRequest(
        String code,
        @JsonProperty("redirect_uri")
        String redirectUri
) {}
