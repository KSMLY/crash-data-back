package com.crashdata.back.controller;

import com.crashdata.back.code.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CodeController {

    private static final Map<String, List<? extends Enum<?>>> CODES = Map.ofEntries(
            Map.entry("alcoholSuspected", List.of(AlcoholSuspected.values())),
            Map.entry("crashSeverity", List.of(CrashSeverity.values())),
            Map.entry("crashType", List.of(CrashType.values())),
            Map.entry("curve", List.of(Curve.values())),
            Map.entry("drugUse", List.of(DrugUse.values())),
            Map.entry("functionalClass", List.of(FunctionalClass.values())),
            Map.entry("grade", List.of(Grade.values())),
            Map.entry("helmet", List.of(Helmet.values())),
            Map.entry("impactType", List.of(ImpactType.values())),
            Map.entry("injurySeverity", List.of(InjurySeverity.values())),
            Map.entry("junctionType", List.of(JunctionType.values())),
            Map.entry("licenceStatus", List.of(LicenceStatus.values())),
            Map.entry("light", List.of(Light.values())),
            Map.entry("manoeuvre", List.of(Manoeuvre.values())),
            Map.entry("obstaclePresent", List.of(ObstaclePresent.values())),
            Map.entry("pedManoeuvre", List.of(PedManoeuvre.values())),
            Map.entry("restraint", List.of(Restraint.values())),
            Map.entry("resultStatus", List.of(ResultStatus.values())),
            Map.entry("roadUserType", List.of(RoadUserType.values())),
            Map.entry("roadwayType", List.of(RoadwayType.values())),
            Map.entry("seatPosition", List.of(SeatPosition.values())),
            Map.entry("seatRow", List.of(SeatRow.values())),
            Map.entry("sex", List.of(Sex.values())),
            Map.entry("specialFunction", List.of(SpecialFunction.values())),
            Map.entry("surfaceCondition", List.of(SurfaceCondition.values())),
            Map.entry("testStatus", List.of(TestStatus.values())),
            Map.entry("testType", List.of(TestType.values())),
            Map.entry("trafficControl", List.of(TrafficControl.values())),
            Map.entry("vehicleType", List.of(VehicleType.values())),
            Map.entry("weather", List.of(Weather.values()))
    );

    @GetMapping("/codes")
    public Map<String, List<? extends Enum<?>>> getCodes() {
        return CODES;
    }

}
