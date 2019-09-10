package com.georent.dto;

import com.georent.validation.StartBeforeEndDate;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@StartBeforeEndDate
public class RentOrderDTO implements TimeRangeable {
    private Long orderId;
    private LotDTO lotDTO;
    private Long renteeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}