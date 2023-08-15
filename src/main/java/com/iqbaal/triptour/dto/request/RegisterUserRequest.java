package com.iqbaal.triptour.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @NotNull
    private Character gender;

    @NotBlank
    @Size(min = 10, max = 13)
    private String phone;

    @NotBlank
    @Size(max = 100)
    private String address;

    private String role;
}
