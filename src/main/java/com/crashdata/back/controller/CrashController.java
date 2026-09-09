package com.crashdata.back.controller;

import com.crashdata.back.code.*;
import com.crashdata.back.dto.AlcoholTestDto;
import com.crashdata.back.dto.CrashDetailDto;
import com.crashdata.back.dto.CrashDto;
import com.crashdata.back.dto.DistrictDto;
import com.crashdata.back.dto.MunicipalityDto;
import com.crashdata.back.dto.AlcoholTestRequest;
import com.crashdata.back.dto.CrashRequest;
import com.crashdata.back.dto.PersonRequest;
import com.crashdata.back.dto.VehicleRequest;
import com.crashdata.back.dto.PersonDto;
import com.crashdata.back.dto.VehicleDto;
import com.crashdata.back.entity.AlcoholTest;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.CrashDetail;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Municipality;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
                request.vehicleType(),
                request.make(),
                request.model(),
                request.modelYear(),
                request.engineCc(),
                request.specialFunction(),
                request.manoeuvre());
    }

    private static PersonSubmission toSubmission(PersonRequest request) {
        Person person = new Person(
                null,
                null,
                request.personNumber(),
                null,
                null,
                request.dateOfBirth(),
                request.sex(),
                request.roadUserType(),
                request.seatRow(),
                request.seatPosition(),
                request.injurySeverity(),
                request.restraint(),
                request.helmet(),
                request.pedManoeuvre(),
                request.alcoholSuspected(),
                request.drugUse(),
                request.licenceStatus(),
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
                request.testStatus(),
                request.testType(),
                request.resultStatus(),
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
                District.ref(request.districtId()),
                Municipality.ref(request.municipalityId()),
                request.latitude(),
                request.longitude(),
                request.crashType(),
                request.impactType(),
                request.weather(),
                request.light(),
                null,
                request.roadwayType(),
                request.functionalClass(),
                request.speedLimitKmh(),
                request.obstaclePresent(),
                request.surfaceCondition(),
                request.junctionType(),
                request.curve(),
                request.grade(),
                Set.copyOf(nullToEmpty(request.trafficControls())));
    }

    private static CrashDto toDto(Crash crash) {
        return new CrashDto(
                crash.getId(),
                crash.getPoliceRef(),
                crash.getRefYear(),
                crash.getCrashDate(),
                crash.getCrashTime(),
                toDto(crash.getDistrict()),
                toDto(crash.getMunicipality()),
                crash.getLatitude(),
                crash.getLongitude(),
                crash.getCrashType(),
                crash.getImpactType(),
                crash.getWeather(),
                crash.getLight(),
                crash.getSeverity(),
                crash.getRoadwayType(),
                crash.getFunctionalClass(),
                crash.getSpeedLimitKmh(),
                crash.getObstaclePresent(),
                crash.getSurfaceCondition(),
                crash.getJunctionType(),
                crash.getCurve(),
                crash.getGrade(),
                crash.getTrafficControls().stream()
                        .sorted(Comparator.comparing(TrafficControl::getCode))
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

    private static DistrictDto toDto(District district) {
        return new DistrictDto(district.getId(), district.getGovernorateId(),
                district.getNameEn(), district.getNameAr());
    }

    private static MunicipalityDto toDto(Municipality municipality) {
        if (municipality == null) return null;
        return new MunicipalityDto(municipality.getId(), municipality.getDistrictId(),
                municipality.getNameEn(), municipality.getNameAr());
    }

    private static VehicleDto toDto(Vehicle vehicle) {
        return new VehicleDto(
                vehicle.getId(),
                vehicle.getVehicleNumber(),
                vehicle.getVehicleType(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getModelYear(),
                vehicle.getEngineCc(),
                vehicle.getSpecialFunction(),
                vehicle.getManoeuvre());
    }

    private static PersonDto toDto(Person person, AlcoholTest test) {
        return new PersonDto(
                person.getId(),
                person.getPersonNumber(),
                person.getOccupantVehicleId(),
                person.getStruckByVehicleId(),
                person.getDateOfBirth(),
                person.getSex(),
                person.getRoadUserType(),
                person.getSeatRow(),
                person.getSeatPosition(),
                person.getInjurySeverity(),
                person.getRestraint(),
                person.getHelmet(),
                person.getPedManoeuvre(),
                person.getAlcoholSuspected(),
                person.getDrugUse(),
                person.getLicenceStatus(),
                person.getLicenceIssueDate(),
                test == null ? null : toDto(test));
    }

    private static AlcoholTestDto toDto(AlcoholTest test) {
        return new AlcoholTestDto(
                test.getTestStatus(),
                test.getTestType(),
                test.getResultStatus(),
                test.getResultValue());
    }
}
