package com.georent.dto;

import java.time.LocalDateTime;

public interface TimeRangeable {

    LocalDateTime getStartTime();
    LocalDateTime getEndTime();

}
