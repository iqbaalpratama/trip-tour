package com.iqbaal.triptour.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripResponse {

    private String name;

    private String city;

    private Integer price;

    private Integer quota;

    private Integer noOfDays;

    private String status;

    private String imagePath;

    private String videoPath;

    private String tncPath;

}
