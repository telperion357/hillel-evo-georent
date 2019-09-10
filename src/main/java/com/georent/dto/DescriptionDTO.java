package com.georent.dto;

import lombok.Data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Data
public class DescriptionDTO {
    private List<Long> pictureIds = new ArrayList<>();
    private List<URL> URLs = new ArrayList<>();
    private String lotName;
    private String lotDescription;
}
