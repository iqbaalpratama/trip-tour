package com.iqbaal.triptour.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("expiresIn")
    private Integer expiresIn;
}
