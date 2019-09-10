package com.georent.dto;

import lombok.Data;

@Data
public class LotShortDTO {
    private Long id;
    private Long price;
    private CoordinatesDTO coordinates;
    private String lotName;
}
