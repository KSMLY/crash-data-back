package com.crashdata.back.entity;

import com.crashdata.back.code.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Crash {

    private Long id;
    private String policeRef;
    private Short refYear;
    private LocalDate crashDate;
    private LocalTime crashTime;
    private District district;
    private Municipality municipality;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private CrashType crashType;
    private ImpactType impactType;
    private Weather weather;
    private Light light;
    private CrashSeverity severity;
    private RoadwayType roadwayType;
    private FunctionalClass functionalClass;
    private Short speedLimitKmh;
    private ObstaclePresent obstaclePresent;
    private SurfaceCondition surfaceCondition;
    private JunctionType junctionType;
    private Curve curve;
    private Grade grade;
    private Set<TrafficControl> trafficControls;

    public Crash withSeverity(CrashSeverity severity) {
        return new Crash(id, policeRef, refYear, crashDate, crashTime, district, municipality,
                latitude, longitude, crashType, impactType, weather, light, severity,
                roadwayType, functionalClass, speedLimitKmh, obstaclePresent, surfaceCondition,
                junctionType, curve, grade, trafficControls);
    }
}
