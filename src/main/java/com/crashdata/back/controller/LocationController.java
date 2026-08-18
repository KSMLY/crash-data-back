package com.crashdata.back.controller;

import com.crashdata.back.dto.DistrictDto;
import com.crashdata.back.dto.GovernorateDto;
import com.crashdata.back.dto.MunicipalityDto;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Governorate;
import com.crashdata.back.entity.Municipality;
import com.crashdata.back.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/governorates")
    public List<GovernorateDto> getGovernorates() {
        return locationService.getGovernorates().stream()
                .map(LocationController::toDto)
                .toList();
    }

    @GetMapping("/districts")
    public List<DistrictDto> getDistricts(@RequestParam(required = false) Long governorateId) {
        return locationService.getDistricts(governorateId).stream()
                .map(LocationController::toDto)
                .toList();
    }

    @GetMapping("/municipalities")
    public List<MunicipalityDto> getMunicipalities(@RequestParam(required = false) Long districtId) {
        return locationService.getMunicipalities(districtId).stream()
                .map(LocationController::toDto)
                .toList();
    }

    private static GovernorateDto toDto(Governorate governorate) {
        return new GovernorateDto(governorate.getId(), governorate.getNameEn(), governorate.getNameAr());
    }

    private static DistrictDto toDto(District district) {
        return new DistrictDto(district.getId(), district.getGovernorateId(), district.getNameEn(), district.getNameAr());
    }

    private static MunicipalityDto toDto(Municipality municipality) {
        return new MunicipalityDto(municipality.getId(), municipality.getDistrictId(), municipality.getNameEn(), municipality.getNameAr());
    }
}
