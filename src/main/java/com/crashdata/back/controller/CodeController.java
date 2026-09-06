package com.crashdata.back.controller;

import com.crashdata.back.code.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class CodeController {

    private static final Map<String, List<Short>> CODES = Map.ofEntries(
            Map.entry("alcoholSuspected", codesOf(AlcoholSuspected.values())),
            Map.entry("crashSeverity", codesOf(CrashSeverity.values())),
            Map.entry("crashType", codesOf(CrashType.values())),
            Map.entry("curve", codesOf(Curve.values())),
            Map.entry("drugUse", codesOf(DrugUse.values())),
            Map.entry("functionalClass", codesOf(FunctionalClass.values())),
            Map.entry("grade", codesOf(Grade.values())),
            Map.entry("helmet", codesOf(Helmet.values())),
            Map.entry("impactType", codesOf(ImpactType.values())),
            Map.entry("injurySeverity", codesOf(InjurySeverity.values())),
            Map.entry("junctionType", codesOf(JunctionType.values())),
            Map.entry("licenceStatus", codesOf(LicenceStatus.values())),
            Map.entry("light", codesOf(Light.values())),
            Map.entry("manoeuvre", codesOf(Manoeuvre.values())),
            Map.entry("obstaclePresent", codesOf(ObstaclePresent.values())),
            Map.entry("pedManoeuvre", codesOf(PedManoeuvre.values())),
            Map.entry("restraint", codesOf(Restraint.values())),
            Map.entry("resultStatus", codesOf(ResultStatus.values())),
            Map.entry("roadUserType", codesOf(RoadUserType.values())),
            Map.entry("roadwayType", codesOf(RoadwayType.values())),
            Map.entry("seatPosition", codesOf(SeatPosition.values())),
            Map.entry("seatRow", codesOf(SeatRow.values())),
            Map.entry("sex", codesOf(Sex.values())),
            Map.entry("specialFunction", codesOf(SpecialFunction.values())),
            Map.entry("surfaceCondition", codesOf(SurfaceCondition.values())),
            Map.entry("testStatus", codesOf(TestStatus.values())),
            Map.entry("testType", codesOf(TestType.values())),
            Map.entry("trafficControl", codesOf(TrafficControl.values())),
            Map.entry("vehicleType", codesOf(VehicleType.values())),
            Map.entry("weather", codesOf(Weather.values()))
    );

    @GetMapping("/codes")
    public Map<String, List<Short>> getCodes() {
        return CODES;
    }

    private static <E extends Enum<E> & CodedEnum> List<Short> codesOf(E[] values) {
        return Arrays.stream(values)
                .map(CodedEnum::getCode)
                .toList();
    }
}
