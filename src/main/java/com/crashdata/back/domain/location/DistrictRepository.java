package com.crashdata.back.domain.location;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findByGovernorateId(Long governorateId);
}
