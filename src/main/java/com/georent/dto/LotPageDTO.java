package com.georent.dto;

import lombok.Data;

import java.net.URL;

@Data
public class LotPageDTO {
    private Long id;
    private Long price;
    private String address;
    private String lotName;
    private URL imageUrl;
}
