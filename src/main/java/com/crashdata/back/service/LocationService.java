package com.crashdata.back.service;

import com.crashdata.back.dao.DistrictDao;
import com.crashdata.back.dao.GovernorateDao;
import com.crashdata.back.dao.MunicipalityDao;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Governorate;
import com.crashdata.back.entity.Municipality;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final GovernorateDao governorateDao;
    private final DistrictDao districtDao;
    private final MunicipalityDao municipalityDao;

        public LocationService(GovernorateDao governorateDao,
                               DistrictDao districtDao,
                               MunicipalityDao municipalityDao){
            this.governorateDao = governorateDao;
            this.districtDao = districtDao;
            this.municipalityDao = municipalityDao;
        }

    public List<Governorate> getGovernorates() {
        return governorateDao.findAll();
    }

    public List<District> getDistricts(Long governorateId) {
        return governorateId == null
                ? districtDao.findAll()
                : districtDao.findByGovernorateId(governorateId);
    }

    public List<Municipality> getMunicipalities(Long districtId) {
        return districtId == null
                ? municipalityDao.findAll()
                : municipalityDao.findByDistrictId(districtId);
    }

}
