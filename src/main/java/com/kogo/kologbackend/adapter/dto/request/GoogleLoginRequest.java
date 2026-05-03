package com.kogo.kologbackend.adapter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleLoginRequest(
        String code,
        @JsonProperty("redirect_uri")
        String redirectUri
) {}
