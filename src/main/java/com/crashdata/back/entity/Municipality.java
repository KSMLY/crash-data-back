package com.crashdata.back.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Municipality {

    private Long id;
    private Long districtId;
    private String nameEn;
    private String nameAr;

    /** A municipality known only by its id, as it arrives on a write request. */
    public static Municipality ref(Long id) {
        return id == null ? null : new Municipality(id, null, null, null);
    }

}
