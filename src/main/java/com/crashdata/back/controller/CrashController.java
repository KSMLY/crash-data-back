package com.crashdata.back.controller;

import com.crashdata.back.code.CodedEnum;
import com.crashdata.back.code.TrafficControl;
import com.crashdata.back.dto.CrashDto;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.service.CrashService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CrashController {

    private final CrashService crashService;

    public CrashController(CrashService crashService) {
        this.crashService = crashService;
    }

    @GetMapping("/crashes")
    public List<CrashDto> getCrashes() {
        return crashService.getCrashes().stream()
                .map(CrashController::toDto)
                .toList();
    }

    @GetMapping("/crashes/{id}")
    public CrashDto getCrash(@PathVariable Long id) {
        return crashService.getCrash(id)
                .map(CrashController::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private static CrashDto toDto(Crash crash) {
        return new CrashDto(
                crash.getId(),
                crash.getPoliceRef(),
                crash.getRefYear(),
                crash.getCrashDate(),
                crash.getCrashTime(),
                crash.getDistrictId(),
                crash.getMunicipalityId(),
                crash.getLatitude(),
                crash.getLongitude(),
                code(crash.getCrashType()),
                code(crash.getImpactType()),
                code(crash.getWeather()),
                code(crash.getLight()),
                code(crash.getSeverity()),
                code(crash.getRoadwayType()),
                code(crash.getFunctionalClass()),
                crash.getSpeedLimitKmh(),
                code(crash.getObstaclePresent()),
                code(crash.getSurfaceCondition()),
                code(crash.getJunctionType()),
                code(crash.getCurve()),
                code(crash.getGrade()),
                crash.getTrafficControls().stream()
                        .map(TrafficControl::getCode)
                        .sorted()
                        .toList());
    }

    private static Short code(CodedEnum value) {
        return value == null ? null : value.getCode();
    }
}
