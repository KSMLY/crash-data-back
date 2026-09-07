package com.crashdata.back.controller;

import com.crashdata.back.code.*;
import com.crashdata.back.dto.AlcoholTestDto;
import com.crashdata.back.dto.CrashDetailDto;
import com.crashdata.back.dto.CrashDto;
import com.crashdata.back.dto.AlcoholTestRequest;
import com.crashdata.back.dto.CrashRequest;
import com.crashdata.back.dto.PersonRequest;
import com.crashdata.back.dto.VehicleRequest;
import com.crashdata.back.dto.PersonDto;
import com.crashdata.back.dto.VehicleDto;
import com.crashdata.back.entity.AlcoholTest;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.CrashDetail;
import com.crashdata.back.entity.PersonSubmission;
import com.crashdata.back.entity.Person;
import com.crashdata.back.entity.Vehicle;
import com.crashdata.back.service.CrashService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.crashdata.back.code.CodedEnum.codeOf;

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
    public ResponseEntity<CrashDetailDto> createCrash(@Valid @RequestBody CrashRequest request) {
        CrashDetail saved = crashService.createCrash(
                toEntity(request),
                nullToEmpty(request.vehicles()).stream().map(CrashController::toEntity).toList(),
                nullToEmpty(request.persons()).stream().map(CrashController::toSubmission).toList());
        CrashDetailDto dto = toDto(saved);
        return ResponseEntity.created(URI.create("/crashes/" + dto.getCrash().id())).body(dto);
    }

    private static Vehicle toEntity(VehicleRequest request) {
        return new Vehicle(
                null,
                null,
                request.vehicleNumber(),
                value(VehicleType.class, request.vehicleTypeCode()),
                request.make(),
                request.model(),
                request.modelYear(),
                request.engineCc(),
                value(SpecialFunction.class, request.specialFunctionCode()),
                value(Manoeuvre.class, request.manoeuvreCode()));
    }

    private static PersonSubmission toSubmission(PersonRequest request) {
        Person person = new Person(
                null,
                null,
                request.personNumber(),
                null,
                null,
                request.dateOfBirth(),
                value(Sex.class, request.sexCode()),
                value(RoadUserType.class, request.roadUserTypeCode()),
                value(SeatRow.class, request.seatRowCode()),
                value(SeatPosition.class, request.seatPositionCode()),
                value(InjurySeverity.class, request.injurySeverityCode()),
                value(Restraint.class, request.restraintCode()),
                value(Helmet.class, request.helmetCode()),
                value(PedManoeuvre.class, request.pedManoeuvreCode()),
                value(AlcoholSuspected.class, request.alcoholSuspectedCode()),
                value(DrugUse.class, request.drugUseCode()),
                value(LicenceStatus.class, request.licenceStatusCode()),
                request.licenceIssueDate());
        return new PersonSubmission(
                person,
                request.occupantVehicleNumber(),
                request.struckByVehicleNumber(),
                toEntity(request.alcoholTest()));
    }

    private static AlcoholTest toEntity(AlcoholTestRequest request) {
        if (request == null) return null;
        return new AlcoholTest(
                null,
                value(TestStatus.class, request.testStatusCode()),
                value(TestType.class, request.testTypeCode()),
                value(ResultStatus.class, request.resultStatusCode()),
                request.resultValue());
    }

    private static <T> List<T> nullToEmpty(List<T> values) {
        return values == null ? List.of() : values;
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
                null,
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
                codeOf(crash.getCrashType()),
                codeOf(crash.getImpactType()),
                codeOf(crash.getWeather()),
                codeOf(crash.getLight()),
                codeOf(crash.getSeverity()),
                codeOf(crash.getRoadwayType()),
                codeOf(crash.getFunctionalClass()),
                crash.getSpeedLimitKmh(),
                codeOf(crash.getObstaclePresent()),
                codeOf(crash.getSurfaceCondition()),
                codeOf(crash.getJunctionType()),
                codeOf(crash.getCurve()),
                codeOf(crash.getGrade()),
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
                codeOf(vehicle.getVehicleType()),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getModelYear(),
                vehicle.getEngineCc(),
                codeOf(vehicle.getSpecialFunction()),
                codeOf(vehicle.getManoeuvre()));
    }

    private static PersonDto toDto(Person person, AlcoholTest test) {
        return new PersonDto(
                person.getId(),
                person.getPersonNumber(),
                person.getOccupantVehicleId(),
                person.getStruckByVehicleId(),
                person.getDateOfBirth(),
                codeOf(person.getSex()),
                codeOf(person.getRoadUserType()),
                codeOf(person.getSeatRow()),
                codeOf(person.getSeatPosition()),
                codeOf(person.getInjurySeverity()),
                codeOf(person.getRestraint()),
                codeOf(person.getHelmet()),
                codeOf(person.getPedManoeuvre()),
                codeOf(person.getAlcoholSuspected()),
                codeOf(person.getDrugUse()),
                codeOf(person.getLicenceStatus()),
                person.getLicenceIssueDate(),
                test == null ? null : toDto(test));
    }

    private static AlcoholTestDto toDto(AlcoholTest test) {
        return new AlcoholTestDto(
                codeOf(test.getTestStatus()),
                codeOf(test.getTestType()),
                codeOf(test.getResultStatus()),
                test.getResultValue());
    }
}
