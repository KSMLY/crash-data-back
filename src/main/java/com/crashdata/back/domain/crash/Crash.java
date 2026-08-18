package com.crashdata.back.domain.crash;

import com.crashdata.back.domain.code.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "crash",
        uniqueConstraints = @UniqueConstraint(name = "uq_crash_police_ref", columnNames = {"ref_year", "police_ref"}))
public class Crash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "police_ref", nullable = false, length = 30)
    private String policeRef;

    @Column(name = "ref_year", nullable = false)
    private Short refYear;

    @Column(name = "crash_date")
    private LocalDate crashDate;

    @Column(name = "crash_time")
    private LocalTime crashTime;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "municipality_id")
    private Long municipalityId;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "crash_type_code", nullable = false)
    private CrashType crashType;

    @Column(name = "impact_type_code", nullable = false)
    private ImpactType impactType;

    @Column(name = "weather_code", nullable = false)
    private Weather weather;

    @Column(name = "light_code", nullable = false)
    private Light light;

    @Column(name = "severity_code", nullable = false)
    private CrashSeverity severity;

    @Column(name = "roadway_type_code", nullable = false)
    private RoadwayType roadwayType;

    @Column(name = "functional_class_code")
    private FunctionalClass functionalClass;

    @Column(name = "speed_limit_kmh", nullable = false)
    private Short speedLimitKmh;

    @Column(name = "obstacle_present_code", nullable = false)
    private ObstaclePresent obstaclePresent;

    @Column(name = "surface_condition_code", nullable = false)
    private SurfaceCondition surfaceCondition;

    @Column(name = "junction_type_code", nullable = false)
    private JunctionType junctionType;

    @Column(name = "curve_code", nullable = false)
    private Curve curve;

    @Column(name = "grade_code", nullable = false)
    private Grade grade;

    @ElementCollection
    @CollectionTable(name = "crash_traffic_control", joinColumns = @JoinColumn(name = "crash_id"))
    @Column(name = "control_code", nullable = false)
    @Convert(converter = TrafficControl.Conv.class)
    private Set<TrafficControl> trafficControls = new HashSet<>();

    protected Crash() { }

    public Crash(String policeRef, Short refYear, LocalDate crashDate, LocalTime crashTime,
                 Long districtId, Long municipalityId, BigDecimal latitude, BigDecimal longitude,
                 CrashType crashType, ImpactType impactType, Weather weather, Light light, CrashSeverity severity,
                 RoadwayType roadwayType, FunctionalClass functionalClass, Short speedLimitKmh,
                 ObstaclePresent obstaclePresent, SurfaceCondition surfaceCondition, JunctionType junctionType,
                 Curve curve, Grade grade) {
        this.policeRef = policeRef;
        this.refYear = refYear;
        this.crashDate = crashDate;
        this.crashTime = crashTime;
        this.districtId = districtId;
        this.municipalityId = municipalityId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.crashType = crashType;
        this.impactType = impactType;
        this.weather = weather;
        this.light = light;
        this.severity = severity;
        this.roadwayType = roadwayType;
        this.functionalClass = functionalClass;
        this.speedLimitKmh = speedLimitKmh;
        this.obstaclePresent = obstaclePresent;
        this.surfaceCondition = surfaceCondition;
        this.junctionType = junctionType;
        this.curve = curve;
        this.grade = grade;
    }

    public void addTrafficControl(TrafficControl control) {
        trafficControls.add(control);
    }

    public Long getId()                             { return id; }
    public String getPoliceRef()                    { return policeRef; }
    public Short getRefYear()                       { return refYear; }
    public LocalDate getCrashDate()                 { return crashDate; }
    public LocalTime getCrashTime()                 { return crashTime; }
    public Long getDistrictId()                   { return districtId; }
    public Long getMunicipalityId()           { return municipalityId; }
    public BigDecimal getLatitude()                 { return latitude; }
    public BigDecimal getLongitude()                { return longitude; }
    public CrashType getCrashType()                 { return crashType; }
    public ImpactType getImpactType()               { return impactType; }
    public Weather getWeather()                     { return weather; }
    public Light getLight()                         { return light; }
    public CrashSeverity getSeverity()               { return severity; }
    public RoadwayType getRoadwayType()              { return roadwayType; }
    public FunctionalClass getFunctionalClass()      { return functionalClass; }
    public Short getSpeedLimitKmh()                  { return speedLimitKmh; }
    public ObstaclePresent getObstaclePresent()      { return obstaclePresent; }
    public SurfaceCondition getSurfaceCondition()    { return surfaceCondition; }
    public JunctionType getJunctionType()            { return junctionType; }
    public Curve getCurve()                          { return curve; }
    public Grade getGrade()                          { return grade; }
    public Set<TrafficControl> getTrafficControls()  { return trafficControls; }
}