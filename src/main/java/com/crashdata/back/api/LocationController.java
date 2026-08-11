package com.crashdata.back.api;

import com.crashdata.back.domain.location.District;
import com.crashdata.back.domain.location.DistrictRepository;
import com.crashdata.back.domain.location.Governorate;
import com.crashdata.back.domain.location.GovernorateRepository;
import com.crashdata.back.domain.location.Municipality;
import com.crashdata.back.domain.location.MunicipalityRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationController {

    private final GovernorateRepository governorateRepository;
    private final DistrictRepository districtRepository;
    private final MunicipalityRepository municipalityRepository;

    public LocationController(GovernorateRepository governorateRepository,
                               DistrictRepository districtRepository,
                               MunicipalityRepository municipalityRepository) {
        this.governorateRepository = governorateRepository;
        this.districtRepository = districtRepository;
        this.municipalityRepository = municipalityRepository;
    }

    @Cacheable("governorates")
    @GetMapping("/governorates")
    public List<GovernorateDto> getGovernorates() {
        return governorateRepository.findAll().stream()
                .map(LocationController::toDto)
                .toList();
    }

    @Cacheable("districts")
    @GetMapping("/districts")
    public List<DistrictDto> getDistricts(@RequestParam(required = false) Long governorateId) {
        List<District> districts = governorateId == null
                ? districtRepository.findAll()
                : districtRepository.findByGovernorateId(governorateId);
        return districts.stream()
                .map(LocationController::toDto)
                .toList();
    }

    @Cacheable("municipalities")
    @GetMapping("/municipalities")
    public List<MunicipalityDto> getMunicipalities(@RequestParam(required = false) Long districtId) {
        List<Municipality> municipalities = districtId == null
                ? municipalityRepository.findAll()
                : municipalityRepository.findByDistrictId(districtId);
        return municipalities.stream()
                .map(LocationController::toDto)
                .toList();
    }

    private static GovernorateDto toDto(Governorate governorate) {
        return new GovernorateDto(governorate.getId(), governorate.getNameEn(), governorate.getNameAr());
    }

    private static DistrictDto toDto(District district) {
        return new DistrictDto(district.getId(), district.getGovernorate().getId(), district.getNameEn(), district.getNameAr());
    }

    private static MunicipalityDto toDto(Municipality municipality) {
        return new MunicipalityDto(municipality.getId(), municipality.getDistrict().getId(), municipality.getNameEn(), municipality.getNameAr());
    }
}
