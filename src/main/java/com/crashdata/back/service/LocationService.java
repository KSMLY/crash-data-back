package com.crashdata.back.service;

import com.crashdata.back.dao.DistrictDao;
import com.crashdata.back.dao.GovernorateDao;
import com.crashdata.back.dao.MunicipalityDao;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Governorate;
import com.crashdata.back.entity.Municipality;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
@AllArgsConstructor
public class LocationService {

    private final GovernorateDao governorateDao;
    private final DistrictDao districtDao;
    private final MunicipalityDao municipalityDao;

    @Cacheable("governorates")
    public List<Governorate> getGovernorates() {
        return governorateDao.findAll();
    }

    @Cacheable("districts")
    public List<District> getDistricts(Long governorateId) {
        return governorateId == null
                ? districtDao.findAll()
                : districtDao.findByGovernorateId(governorateId);
    }

    @Cacheable("municipalities")
    public List<Municipality> getMunicipalities(Long districtId) {
        return districtId == null
                ? municipalityDao.findAll()
                : municipalityDao.findByDistrictId(districtId);
    }

}
