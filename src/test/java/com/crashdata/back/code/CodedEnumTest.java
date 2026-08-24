package com.crashdata.back.code;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CodedEnumTest {

    static Stream<Class<? extends CodedEnum>> enums() {
        return Stream.of(
                Weather.class,
                CrashType.class,
                ImpactType.class,
                Light.class,
                CrashSeverity.class,
                RoadwayType.class,
                FunctionalClass.class,
                ObstaclePresent.class,
                SurfaceCondition.class,
                JunctionType.class,
                Curve.class,
                Grade.class,
                TrafficControl.class,
                VehicleType.class,
                SpecialFunction.class,
                Manoeuvre.class,
                Sex.class,
                RoadUserType.class,
                SeatRow.class,
                SeatPosition.class,
                InjurySeverity.class,
                Restraint.class,
                Helmet.class,
                PedManoeuvre.class,
                AlcoholSuspected.class,
                DrugUse.class,
                TestStatus.class,
                TestType.class,
                ResultStatus.class,
                LicenceStatus.class
        );
    }

    @ParameterizedTest
    @MethodSource("enums")
    void codesAreUniqueWithinEnum(Class<? extends CodedEnum> type) {
        Set<Short> codes = new HashSet<>();
        for (CodedEnum constant : type.getEnumConstants()) {
            assertTrue(codes.add(constant.getCode()),
                    () -> type.getSimpleName() + " reuses code " + constant.getCode());
        }
    }

    @ParameterizedTest
    @MethodSource("enums")
    <E extends Enum<E> & CodedEnum> void everyConstantRoundTripsThroughFromCode(Class<E> type) {
        for (E constant : type.getEnumConstants()) {
            assertEquals(constant, CodedEnum.fromCode(type, constant.getCode()));
        }
    }
}
