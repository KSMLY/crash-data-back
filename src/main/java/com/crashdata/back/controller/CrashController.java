package com.crashdata.back.controller;

import com.crashdata.back.code.*;
import com.crashdata.back.dto.CrashDto;
import com.crashdata.back.dto.CrashRequest;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.service.CrashService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class CrashController {

    private final CrashService crashService;

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

    @PostMapping("/crashes")
    public ResponseEntity<CrashDto> createCrash(@RequestBody CrashRequest request) {
        Crash saved = crashService.createCrash(toEntity(request));
        CrashDto dto = toDto(saved);
        return ResponseEntity.created(URI.create("/crashes/" + dto.id())).body(dto);
    }

    private static Crash toEntity(CrashRequest request) {
        return new Crash(
                null,
                request.policeRef(),
                request.refYear(),
                request.crashDate(),
                request.crashTime(),
                request.districtId(),
                request.municipalityId(),
                request.latitude(),
                request.longitude(),
                value(CrashType.class, request.crashTypeCode()),
                value(ImpactType.class, request.impactTypeCode()),
                value(Weather.class, request.weatherCode()),
                value(Light.class, request.lightCode()),
                value(CrashSeverity.class, request.severityCode()),
                value(RoadwayType.class, request.roadwayTypeCode()),
                value(FunctionalClass.class, request.functionalClassCode()),
                request.speedLimitKmh(),
                value(ObstaclePresent.class, request.obstaclePresentCode()),
                value(SurfaceCondition.class, request.surfaceConditionCode()),
                value(JunctionType.class, request.junctionTypeCode()),
                value(Curve.class, request.curveCode()),
                value(Grade.class, request.gradeCode()),
                controls(request.trafficControlCodes()));
    }

    private static <E extends Enum<E> & CodedEnum> E value(Class<E> type, Short code) {
        return code == null ? null : CodedEnum.fromCode(type, code);
    }

    private static Set<TrafficControl> controls(List<Short> codes) {
        if (codes == null) return Set.of();
        return codes.stream()
                .map(code -> value(TrafficControl.class, code))
                .collect(Collectors.toSet());
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
