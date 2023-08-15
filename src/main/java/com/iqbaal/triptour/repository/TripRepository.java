package com.iqbaal.triptour.repository;

import com.iqbaal.triptour.entity.Trip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
}
