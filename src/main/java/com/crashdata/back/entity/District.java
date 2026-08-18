package com.crashdata.back.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class District {

    private Long id;
    private Long governorateId;
    private String nameEn;
    private String nameAr;

}