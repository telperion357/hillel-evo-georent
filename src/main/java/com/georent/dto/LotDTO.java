package com.georent.dto;

import lombok.Data;

@Data
public class LotDTO {
    private Long id;
    private Long price;
    private CoordinatesDTO coordinates;
    private DescriptionDTO description;
}