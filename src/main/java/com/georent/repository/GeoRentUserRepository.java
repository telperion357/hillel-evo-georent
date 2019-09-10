package com.georent.repository;

import com.georent.domain.GeoRentUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeoRentUserRepository extends JpaRepository<GeoRentUser, Long> {

    Optional<GeoRentUser> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<GeoRentUser> findByRecoveryToken(String recoveryToken);

}
