package com.georent.dto;

import com.georent.validation.StartBeforeEndDate;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDate
public class RentOrderRequestDTO implements TimeRangeable {

    @NotNull
    private Long lotId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;
}
