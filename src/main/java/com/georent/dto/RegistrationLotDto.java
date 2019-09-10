package com.georent.dto;

import lombok.Data;

@Data
public class RegistrationLotDto {

    private Long price;

    //    Coordinates
    private Float longitude;
    private Float latitude;
    private String address;

    //         Description
    private String lotName;
    private String lotDescription;

}
