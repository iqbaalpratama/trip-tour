package com.iqbaal.triptour.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String email;

    private String role;

    private String firstName;

    private String lastName;

    private String gender;

    private String phone;

    private String address;
}
