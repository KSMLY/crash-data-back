package com.crashdata.back.domain.location;

import jakarta.persistence.*;

@Entity
@Table(name = "municipality")
public class Municipality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "name_en", nullable = false, length = 150)
    private String nameEn;

    @Column(name = "name_ar", nullable = false, length = 150)
    private String nameAr;


    protected Municipality() {
    }

    public Municipality(District district, String nameEn, String nameAr) {
        this.district = district;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
    }

    public Long getId() {
        return id;
    }

    public District getDistrict() {
        return district;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }
}
