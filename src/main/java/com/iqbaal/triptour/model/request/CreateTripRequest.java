package com.iqbaal.triptour.model.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTripRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotNull
    private Integer price;

    @NotNull
    private Integer quota;

    @NotNull
    private Integer noOfDays;

    @NotBlank
    private String status;

}
