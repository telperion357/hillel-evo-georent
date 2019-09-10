package com.georent.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;


@Data
@Entity
public class RentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long lotId;
    private long renteeId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private RentOrderStatus status;
}
