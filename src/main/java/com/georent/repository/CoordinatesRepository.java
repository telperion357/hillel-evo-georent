package com.georent.repository;

import com.georent.domain.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  CoordinatesRepository extends JpaRepository<Coordinates, Long> {
}
