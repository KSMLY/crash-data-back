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

    /** A district known only by its id, as it arrives on a write request. */
    public static District ref(Long id) {
        return id == null ? null : new District(id, null, null, null);
    }

}
