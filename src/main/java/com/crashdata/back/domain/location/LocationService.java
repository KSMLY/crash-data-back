package com.crashdata.back.domain.location;


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
