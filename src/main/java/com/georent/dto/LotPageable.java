package com.georent.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class LotPageable {

    List<LotPageDTO> lots;
    int pageNumber;
    int totalPages;

    public LotPageable(List<LotPageDTO> lots, int pageNumber, int totalPages) {
        this.lots = lots;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
    }
}
