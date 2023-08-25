package com.iqbaal.triptour.repository;

import java.util.Optional;

import com.iqbaal.triptour.entity.Role;
import com.iqbaal.triptour.entity.utils.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(EnumRole name);
}
