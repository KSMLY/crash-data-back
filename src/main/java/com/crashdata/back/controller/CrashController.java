package com.crashdata.back.controller;

import com.crashdata.back.code.*;
import com.crashdata.back.dto.AlcoholTestDto;
import com.crashdata.back.dto.CrashDetailDto;
import com.crashdata.back.dto.CrashDto;
import com.crashdata.back.dto.CrashRequest;
import com.crashdata.back.dto.PersonDto;
import com.crashdata.back.dto.VehicleDto;
import com.crashdata.back.entity.AlcoholTest;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.CrashDetail;
import com.crashdata.back.entity.Person;
import com.crashdata.back.entity.Vehicle;
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
    public CrashDetailDto getCrash(@PathVariable Long id) {
        return crashService.getCrashDetail(id)
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

    private static CrashDetailDto toDto(CrashDetail detail) {
        return new CrashDetailDto(
                toDto(detail.crash()),
                detail.vehicles().stream()
                        .map(CrashController::toDto)
                        .toList(),
                detail.persons().stream()
                        .map(person -> toDto(person, detail.testsByPersonId().get(person.getId())))
                        .toList());
    }

    private static VehicleDto toDto(Vehicle vehicle) {
        return new VehicleDto(
                vehicle.getId(),
                vehicle.getVehicleNumber(),
                code(vehicle.getVehicleType()),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getModelYear(),
                vehicle.getEngineCc(),
                code(vehicle.getSpecialFunction()),
                code(vehicle.getManoeuvre()));
    }

    private static PersonDto toDto(Person person, AlcoholTest test) {
        return new PersonDto(
                person.getId(),
                person.getPersonNumber(),
                person.getOccupantVehicleId(),
                person.getStruckByVehicleId(),
                person.getDateOfBirth(),
                code(person.getSex()),
                code(person.getRoadUserType()),
                code(person.getSeatRow()),
                code(person.getSeatPosition()),
                code(person.getInjurySeverity()),
                code(person.getRestraint()),
                code(person.getHelmet()),
                code(person.getPedManoeuvre()),
                code(person.getAlcoholSuspected()),
                code(person.getDrugUse()),
                code(person.getLicenceStatus()),
                person.getLicenceIssueDate(),
                test == null ? null : toDto(test));
    }

    private static AlcoholTestDto toDto(AlcoholTest test) {
        return new AlcoholTestDto(
                code(test.getTestStatus()),
                code(test.getTestType()),
                code(test.getResultStatus()),
                test.getResultValue());
    }

    private static Short code(CodedEnum value) {
        return value == null ? null : value.getCode();
    }
}
