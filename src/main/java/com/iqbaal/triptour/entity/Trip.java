package com.iqbaal.triptour.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quota;

    @Column(nullable = false)
    private Integer noOfDays;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String video;

    @Column(nullable = false)
    private String tnc;

    @Column(nullable = false)
    protected ZonedDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by", nullable = true)
    private User user;

}
