package com.iqbaal.triptour.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Getter
@Setter
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
