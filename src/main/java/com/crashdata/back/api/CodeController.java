package com.crashdata.back.api;

import com.crashdata.back.domain.code.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class CodeController {

    @GetMapping("/codes")
    public Map<String, List<CodeDto>> getCodes() {
        return Map.ofEntries(
                Map.entry("alcoholSuspected", toDtos(AlcoholSuspected.values())),
                Map.entry("crashSeverity", toDtos(CrashSeverity.values())),
                Map.entry("crashType", toDtos(CrashType.values())),
                Map.entry("curve", toDtos(Curve.values())),
                Map.entry("drugUse", toDtos(DrugUse.values())),
                Map.entry("functionalClass", toDtos(FunctionalClass.values())),
                Map.entry("grade", toDtos(Grade.values())),
                Map.entry("helmet", toDtos(Helmet.values())),
                Map.entry("impactType", toDtos(ImpactType.values())),
                Map.entry("injurySeverity", toDtos(InjurySeverity.values())),
                Map.entry("junctionType", toDtos(JunctionType.values())),
                Map.entry("licenceStatus", toDtos(LicenceStatus.values())),
                Map.entry("light", toDtos(Light.values())),
                Map.entry("manoeuvre", toDtos(Manoeuvre.values())),
                Map.entry("obstaclePresent", toDtos(ObstaclePresent.values())),
                Map.entry("pedManoeuvre", toDtos(PedManoeuvre.values())),
                Map.entry("restraint", toDtos(Restraint.values())),
                Map.entry("resultStatus", toDtos(ResultStatus.values())),
                Map.entry("roadUserType", toDtos(RoadUserType.values())),
                Map.entry("roadwayType", toDtos(RoadwayType.values())),
                Map.entry("seatPosition", toDtos(SeatPosition.values())),
                Map.entry("seatRow", toDtos(SeatRow.values())),
                Map.entry("sex", toDtos(Sex.values())),
                Map.entry("specialFunction", toDtos(SpecialFunction.values())),
                Map.entry("surfaceCondition", toDtos(SurfaceCondition.values())),
                Map.entry("testStatus", toDtos(TestStatus.values())),
                Map.entry("testType", toDtos(TestType.values())),
                Map.entry("trafficControl", toDtos(TrafficControl.values())),
                Map.entry("vehicleType", toDtos(VehicleType.values())),
                Map.entry("weather", toDtos(Weather.values()))
        );
    }

    private static <E extends Enum<E> & CodedEnum> List<CodeDto> toDtos(E[] values) {
        return Arrays.stream(values)
                .map(v -> new CodeDto(v.getCode(), v.name()))
                .toList();
    }
}
