package com.crashdata.back.dao;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.Crash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CrashDao.class)
class CrashDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CrashDao crashDao;

    // district_id is NOT NULL with a foreign key, so the tests borrow rows Flyway seeded
    private Long districtId;
    private Long municipalityId;

    @BeforeEach
    void readSeededAdminDivisions() {
        districtId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM district", Long.class);
        municipalityId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM municipality", Long.class);
    }

    // Seeded with raw SQL rather than the DAO, so the read tests cannot be fooled by
    // an insert that is wrong in the same way as the select
    private static final String SEED_FULL = """
            INSERT INTO crash (police_ref, ref_year, crash_date, crash_time,
                               district_id, municipality_id, latitude, longitude,
                               crash_type_code, impact_type_code, weather_code, light_code,
                               severity_code, roadway_type_code, functional_class_code,
                               speed_limit_kmh, obstacle_present_code, surface_condition_code,
                               junction_type_code, curve_code, grade_code)
            VALUES (?, ?, '2024-03-14', '13:45:00', ?, ?, 33.5, 35.25,
                    5, 10, 4, 2, 3, 6, 3, 80, 9, 5, 6, 2, 2)""";

    // Every nullable column left out: municipality_id, both dates, both coordinates,
    // functional_class_code
    private static final String SEED_MINIMAL = """
            INSERT INTO crash (police_ref, ref_year, district_id,
                               crash_type_code, impact_type_code, weather_code, light_code,
                               severity_code, roadway_type_code, speed_limit_kmh,
                               obstacle_present_code, surface_condition_code,
                               junction_type_code, curve_code, grade_code)
            VALUES (?, ?, ?, 5, 10, 4, 2, 3, 6, 80, 9, 5, 6, 2, 2)""";

    private long seedFullCrash(String policeRef, int refYear) {
        jdbcTemplate.update(SEED_FULL, policeRef, refYear, districtId, municipalityId);
        return crashId(policeRef, refYear);
    }

    private long seedMinimalCrash(String policeRef, int refYear) {
        jdbcTemplate.update(SEED_MINIMAL, policeRef, refYear, districtId);
        return crashId(policeRef, refYear);
    }

    private long crashId(String policeRef, int refYear) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM crash WHERE ref_year = ? AND police_ref = ?",
                Long.class, refYear, policeRef);
    }

    private void seedControls(long crashId, int... codes) {
        for (int code : codes) {
            jdbcTemplate.update(
                    "INSERT INTO crash_traffic_control (crash_id, control_code) VALUES (?, ?)",
                    crashId, code);
        }
    }

    private static short shortOf(Object value) {
        return ((Number) value).shortValue();
    }

    private static long longOf(Object value) {
        return ((Number) value).longValue();
    }

    private static void assertDecimal(String expected, Object actual) {
        assertEquals(0, new BigDecimal(expected).compareTo((BigDecimal) actual),
                "expected " + expected + " but was " + actual);
    }

    @Test
    void findByIdMapsEveryColumn() {
        long id = seedFullCrash("CD28-FULL", 2024);
        seedControls(id, 3, 8);

        Crash crash = crashDao.findById(id).orElseThrow();

        assertEquals(id, crash.getId());
        assertEquals("CD28-FULL", crash.getPoliceRef());
        assertEquals((short) 2024, crash.getRefYear());
        assertEquals(LocalDate.of(2024, 3, 14), crash.getCrashDate());
        assertEquals(LocalTime.of(13, 45), crash.getCrashTime());
        assertEquals(districtId, crash.getDistrictId());
        assertEquals(municipalityId, crash.getMunicipalityId());
        assertDecimal("33.5", crash.getLatitude());
        assertDecimal("35.25", crash.getLongitude());
        assertEquals(CrashType.ANIMAL, crash.getCrashType());
        assertEquals(ImpactType.REAR_TO_SIDE, crash.getImpactType());
        assertEquals(Weather.FOG, crash.getWeather());
        assertEquals(Light.TWILIGHT, crash.getLight());
        assertEquals(CrashSeverity.SLIGHT, crash.getSeverity());
        assertEquals(RoadwayType.RESTRICTED_ROAD, crash.getRoadwayType());
        assertEquals(FunctionalClass.COLLECTOR, crash.getFunctionalClass());
        assertEquals((short) 80, crash.getSpeedLimitKmh());
        assertEquals(ObstaclePresent.UNKNOWN, crash.getObstaclePresent());
        assertEquals(SurfaceCondition.FLOOD, crash.getSurfaceCondition());
        assertEquals(JunctionType.NOT_AT_GRADE, crash.getJunctionType());
        assertEquals(Curve.OPEN, crash.getCurve());
        assertEquals(Grade.NO, crash.getGrade());
        assertEquals(Set.of(TrafficControl.GIVE_WAY, TrafficControl.OTHER), crash.getTrafficControls());
    }

    // getShort returns 0 for SQL NULL, so a mapper that skips the null check reports
    // a real code where there is none
    @Test
    void findByIdLeavesNullableColumnsNull() {
        long id = seedMinimalCrash("CD28-MIN", 2024);

        Crash crash = crashDao.findById(id).orElseThrow();

        assertNull(crash.getMunicipalityId());
        assertNull(crash.getCrashDate());
        assertNull(crash.getCrashTime());
        assertNull(crash.getLatitude());
        assertNull(crash.getLongitude());
        assertNull(crash.getFunctionalClass());
        assertEquals(Set.of(), crash.getTrafficControls());
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertEquals(Optional.empty(), crashDao.findById(Long.MAX_VALUE));
    }

    @Test
    void findAllOrdersByYearThenPoliceRef() {
        seedFullCrash("CD28-B", 2024);
        seedFullCrash("CD28-A", 2024);
        seedFullCrash("CD28-C", 2023);

        List<String> seeded = crashDao.findAll().stream()
                .map(Crash::getPoliceRef)
                .filter(ref -> ref.startsWith("CD28-"))
                .toList();

        assertEquals(List.of("CD28-C", "CD28-A", "CD28-B"), seeded);
    }

    // One query loads the controls for every crash, so a wrong key would hand a crash
    // another crash's controls
    @Test
    void findAllAttachesControlsToTheRightCrash() {
        long first = seedFullCrash("CD28-CTRL-1", 2024);
        long second = seedFullCrash("CD28-CTRL-2", 2024);
        seedControls(first, 2);
        seedControls(second, 5, 7);

        Map<Long, Set<TrafficControl>> controls = crashDao.findAll().stream()
                .filter(crash -> crash.getPoliceRef().startsWith("CD28-CTRL-"))
                .collect(Collectors.toMap(Crash::getId, Crash::getTrafficControls));

        assertEquals(Set.of(TrafficControl.STOP_SIGN), controls.get(first));
        assertEquals(Set.of(TrafficControl.SIGNAL_WORKING, TrafficControl.UNCONTROLLED),
                controls.get(second));
    }

    @Test
    void insertWritesEveryColumnAndReturnsTheGeneratedId() {
        Crash crash = new Crash(
                null, "CD28-INS", (short) 2024, LocalDate.of(2024, 3, 14), LocalTime.of(13, 45),
                districtId, municipalityId, new BigDecimal("33.5"), new BigDecimal("35.25"),
                CrashType.ANIMAL, ImpactType.REAR_TO_SIDE, Weather.FOG, Light.TWILIGHT,
                CrashSeverity.SLIGHT, RoadwayType.RESTRICTED_ROAD, FunctionalClass.COLLECTOR,
                (short) 80, ObstaclePresent.UNKNOWN, SurfaceCondition.FLOOD,
                JunctionType.NOT_AT_GRADE, Curve.OPEN, Grade.NO,
                Set.of(TrafficControl.GIVE_WAY, TrafficControl.OTHER));

        Long id = crashDao.insert(crash);

        // Read back with plain SQL: going through findById would hide a column that the
        // insert and the select get wrong in the same way
        Map<String, Object> row = jdbcTemplate.queryForMap("SELECT * FROM crash WHERE id = ?", id);
        assertEquals("CD28-INS", row.get("police_ref"));
        assertEquals((short) 2024, shortOf(row.get("ref_year")));
        assertEquals(districtId, longOf(row.get("district_id")));
        assertEquals(municipalityId, longOf(row.get("municipality_id")));
        assertDecimal("33.5", row.get("latitude"));
        assertDecimal("35.25", row.get("longitude"));
        assertEquals((short) 5, shortOf(row.get("crash_type_code")));
        assertEquals((short) 10, shortOf(row.get("impact_type_code")));
        assertEquals((short) 4, shortOf(row.get("weather_code")));
        assertEquals((short) 2, shortOf(row.get("light_code")));
        assertEquals((short) 3, shortOf(row.get("severity_code")));
        assertEquals((short) 6, shortOf(row.get("roadway_type_code")));
        assertEquals((short) 3, shortOf(row.get("functional_class_code")));
        assertEquals((short) 80, shortOf(row.get("speed_limit_kmh")));
        assertEquals((short) 9, shortOf(row.get("obstacle_present_code")));
        assertEquals((short) 5, shortOf(row.get("surface_condition_code")));
        assertEquals((short) 6, shortOf(row.get("junction_type_code")));
        assertEquals((short) 2, shortOf(row.get("curve_code")));
        assertEquals((short) 2, shortOf(row.get("grade_code")));
        assertEquals(LocalDate.of(2024, 3, 14),
                jdbcTemplate.queryForObject("SELECT crash_date FROM crash WHERE id = ?", LocalDate.class, id));
        assertEquals(LocalTime.of(13, 45),
                jdbcTemplate.queryForObject("SELECT crash_time FROM crash WHERE id = ?", LocalTime.class, id));

        List<Short> controls = jdbcTemplate.queryForList(
                "SELECT control_code FROM crash_traffic_control WHERE crash_id = ? ORDER BY control_code",
                Short.class, id);
        assertEquals(List.of((short) 3, (short) 8), controls);
    }

    @Test
    void insertWritesNoControlRowsWhenThereAreNone() {
        Crash crash = new Crash(
                null, "CD28-NOCTRL", (short) 2024, null, null, districtId, null, null, null,
                CrashType.ANIMAL, ImpactType.REAR_TO_SIDE, Weather.FOG, Light.TWILIGHT,
                CrashSeverity.SLIGHT, RoadwayType.RESTRICTED_ROAD, null, (short) 80,
                ObstaclePresent.UNKNOWN, SurfaceCondition.FLOOD, JunctionType.NOT_AT_GRADE,
                Curve.OPEN, Grade.NO, Set.of());

        Long id = crashDao.insert(crash);

        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM crash_traffic_control WHERE crash_id = ?", Integer.class, id));
        assertNull(jdbcTemplate.queryForObject(
                "SELECT municipality_id FROM crash WHERE id = ?", Long.class, id));
        assertNull(jdbcTemplate.queryForObject(
                "SELECT functional_class_code FROM crash WHERE id = ?", Short.class, id));
    }

    // uq_crash_police_ref is (ref_year, police_ref), so the same ref in another year is fine
    @Test
    void insertRejectsADuplicatePoliceRefInTheSameYear() {
        seedFullCrash("CD28-DUPE", 2024);

        Crash duplicate = new Crash(
                null, "CD28-DUPE", (short) 2024, null, null, districtId, null, null, null,
                CrashType.ANIMAL, ImpactType.REAR_TO_SIDE, Weather.FOG, Light.TWILIGHT,
                CrashSeverity.SLIGHT, RoadwayType.RESTRICTED_ROAD, null, (short) 80,
                ObstaclePresent.UNKNOWN, SurfaceCondition.FLOOD, JunctionType.NOT_AT_GRADE,
                Curve.OPEN, Grade.NO, Set.of());

        assertThrows(DuplicateKeyException.class, () -> crashDao.insert(duplicate));
    }

    @Test
    void findAllIncludesAFreshlyInsertedCrash() {
        Long id = crashDao.insert(new Crash(
                null, "CD28-ROUND", (short) 2024, null, null, districtId, null, null, null,
                CrashType.ANIMAL, ImpactType.REAR_TO_SIDE, Weather.FOG, Light.TWILIGHT,
                CrashSeverity.SLIGHT, RoadwayType.RESTRICTED_ROAD, null, (short) 80,
                ObstaclePresent.UNKNOWN, SurfaceCondition.FLOOD, JunctionType.NOT_AT_GRADE,
                Curve.OPEN, Grade.NO, Set.of(TrafficControl.STOP_SIGN)));

        assertTrue(crashDao.findAll().stream().anyMatch(crash -> crash.getId().equals(id)));
        assertEquals(Set.of(TrafficControl.STOP_SIGN),
                crashDao.findById(id).orElseThrow().getTrafficControls());
    }
}
