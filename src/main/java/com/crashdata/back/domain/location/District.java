package com.crashdata.back.domain.location;

import jakarta.persistence.*;

@Entity
@Table(name = "district")
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "governorate_id", nullable = false)
    private Governorate governorate;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "name_ar", nullable = false, length = 100)
    private String nameAr;

    protected District() { }

    public District(Governorate governorate, String nameEn, String nameAr) {
        this.governorate = governorate;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
    }

    public Long getId()                 { return id; }
    public Governorate getGovernorate() { return governorate; }
    public String getNameEn()           { return nameEn; }
    public String getNameAr()           { return nameAr; }
}