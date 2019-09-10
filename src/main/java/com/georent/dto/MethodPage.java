package com.georent.dto;

import lombok.Getter;


@Getter
public enum MethodPage {

    FIRST("first"),
    NEXT("next"),
    PREV("prev"),
    PREVIOUS("previous"),
    PREVIOUS_OR_FIRST("previousOrFirst"),
    LAST("last"),
    CUR("cur");

    private String typeValue;

    private MethodPage(String type) {
        typeValue = type;
    }

    static public MethodPage getType(String pType) {
        for (MethodPage type: MethodPage.values()) {
            if (type.getTypeValue().equals(pType)) {
                return type;
            }
        }
        return MethodPage.valueOf("CUR");
    }

    public String getTypeValue() {
        return typeValue;
    }
}
