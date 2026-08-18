package com.crashdata.back.domain.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Municipality {

    private Long id;
    private Long districtId;
    private String nameEn;
    private String nameAr;

}
