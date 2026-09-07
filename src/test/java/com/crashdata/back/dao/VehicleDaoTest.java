package com.crashdata.back.dao;

import com.crashdata.back.code.Manoeuvre;
import com.crashdata.back.code.SpecialFunction;
import com.crashdata.back.code.VehicleType;
import com.crashdata.back.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(VehicleDao.class)
class VehicleDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    VehicleDao vehicleDao;

    private long crashId;

    @BeforeEach
    void seedCrash() {
        crashId = Seed.crash(jdbcTemplate, "CD28-VEH", Seed.districtId(jdbcTemplate));
    }

    private static final String SEED_FULL = """
            INSERT INTO vehicle (crash_id, vehicle_number, vehicle_type_code,
                                 make, model, model_year, engine_cc,
                                 special_function_code, manoeuvre_code)
            VALUES (?, ?, 5, 'Toyota', 'Corolla', 2019, 1600, 2, 12)""";

    // make, model, model_year and engine_cc are the nullable ones
    private static final String SEED_MINIMAL = """
            INSERT INTO vehicle (crash_id, vehicle_number, vehicle_type_code,
                                 special_function_code, manoeuvre_code)
            VALUES (?, ?, 5, 2, 12)""";

    @Test
    void findByCrashIdMapsEveryColumn() {
        jdbcTemplate.update(SEED_FULL, crashId, 1);

        Vehicle vehicle = vehicleDao.findByCrashId(crashId).getFirst();

        assertEquals(crashId, vehicle.getCrashId());
        assertEquals((short) 1, vehicle.getVehicleNumber());
        assertEquals(VehicleType.BUS, vehicle.getVehicleType());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals((short) 2019, vehicle.getModelYear());
        assertEquals(1600, vehicle.getEngineCc());
        assertEquals(SpecialFunction.TAXI, vehicle.getSpecialFunction());
        assertEquals(Manoeuvre.OVERTAKING, vehicle.getManoeuvre());
        assertEquals(vehicle.getId(), jdbcTemplate.queryForObject(
                "SELECT id FROM vehicle WHERE crash_id = ? AND vehicle_number = 1", Long.class, crashId));
    }

    // A bicycle has no engine, so these come back null rather than 0
    @Test
    void findByCrashIdLeavesNullableColumnsNull() {
        jdbcTemplate.update(SEED_MINIMAL, crashId, 1);

        Vehicle vehicle = vehicleDao.findByCrashId(crashId).getFirst();

        assertNull(vehicle.getMake());
        assertNull(vehicle.getModel());
        assertNull(vehicle.getModelYear());
        assertNull(vehicle.getEngineCc());
    }

    @Test
    void findByCrashIdOrdersByVehicleNumber() {
        jdbcTemplate.update(SEED_MINIMAL, crashId, 3);
        jdbcTemplate.update(SEED_MINIMAL, crashId, 1);
        jdbcTemplate.update(SEED_MINIMAL, crashId, 2);

        List<Short> numbers = vehicleDao.findByCrashId(crashId).stream()
                .map(Vehicle::getVehicleNumber)
                .toList();

        assertEquals(List.of((short) 1, (short) 2, (short) 3), numbers);
    }

    @Test
    void findByCrashIdIgnoresOtherCrashes() {
        long otherCrashId = Seed.crash(jdbcTemplate, "CD28-VEH-OTHER", Seed.districtId(jdbcTemplate));
        jdbcTemplate.update(SEED_MINIMAL, crashId, 1);
        jdbcTemplate.update(SEED_MINIMAL, otherCrashId, 1);

        assertEquals(1, vehicleDao.findByCrashId(crashId).size());
        assertEquals(crashId, vehicleDao.findByCrashId(crashId).getFirst().getCrashId());
    }

    @Test
    void findByCrashIdReturnsEmptyWhenTheCrashHasNoVehicles() {
        assertEquals(List.of(), vehicleDao.findByCrashId(crashId));
    }

    @Test
    void insertWritesEveryColumnAndReturnsTheGeneratedId() {
        Vehicle vehicle = new Vehicle(null, crashId, (short) 1, VehicleType.BUS, "Toyota", "Corolla",
                (short) 2019, 1600, SpecialFunction.TAXI, Manoeuvre.OVERTAKING);

        Long id = vehicleDao.insert(vehicle);

        Map<String, Object> row = jdbcTemplate.queryForMap("SELECT * FROM vehicle WHERE id = ?", id);
        assertEquals(crashId, ((Number) row.get("crash_id")).longValue());
        assertEquals((short) 1, ((Number) row.get("vehicle_number")).shortValue());
        assertEquals((short) 5, ((Number) row.get("vehicle_type_code")).shortValue());
        assertEquals("Toyota", row.get("make"));
        assertEquals("Corolla", row.get("model"));
        assertEquals((short) 2019, ((Number) row.get("model_year")).shortValue());
        assertEquals(1600, ((Number) row.get("engine_cc")).intValue());
        assertEquals((short) 2, ((Number) row.get("special_function_code")).shortValue());
        assertEquals((short) 12, ((Number) row.get("manoeuvre_code")).shortValue());
    }

    @Test
    void insertWritesNullsForAVehicleWithNoEngine() {
        Long id = vehicleDao.insert(new Vehicle(null, crashId, (short) 1, VehicleType.BICYCLE,
                null, null, null, null, SpecialFunction.NONE, Manoeuvre.STRAIGHT_FORWARD));

        Map<String, Object> row = jdbcTemplate.queryForMap("SELECT * FROM vehicle WHERE id = ?", id);
        assertNull(row.get("make"));
        assertNull(row.get("model"));
        assertNull(row.get("model_year"));
        assertNull(row.get("engine_cc"));
    }
}
