package com.georent.repository;

import com.georent.domain.Lot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long>, PagingAndSortingRepository<Lot, Long> {

    List<Lot> findAllByGeoRentUser_Id(Long userId);
    Optional<Lot> findByIdAndGeoRentUser_Id(Long id, Long userId);
    void deleteAllByGeoRentUser_Id(Long userId);

    Page<Lot> findAll (Pageable pageable);
    Page<Lot> findByIdIn(List<Long> ids, Pageable pageable);
}
