package com.crashdata.back.domain.location;

import jakarta.persistence.*;

@Entity
@Table(name = "governorate")
public class Governorate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "name_ar", nullable = false, length = 100)
    private String nameAr;

    protected Governorate() { }

    public Governorate(String nameEn, String nameAr) {
        this.nameEn = nameEn;
        this.nameAr = nameAr;
    }

    public Long getId()       { return id; }
    public String getNameEn() { return nameEn; }
    public String getNameAr() { return nameAr; }
}