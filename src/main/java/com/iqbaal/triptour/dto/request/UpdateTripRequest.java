package com.iqbaal.triptour.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTripRequest {

    private String name;

    private String city;

    private Integer price;

    private Integer quota;

    private Integer noOfDays;

    private String status;
}
