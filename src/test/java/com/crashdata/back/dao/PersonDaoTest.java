package com.crashdata.back.dao;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersonDao.class)
class PersonDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PersonDao personDao;

    private long crashId;
    private long occupantVehicleId;
    private long struckByVehicleId;

    @BeforeEach
    void seedCrashAndVehicles() {
        crashId = Seed.crash(jdbcTemplate, "CD28-PER", Seed.districtId(jdbcTemplate));
        occupantVehicleId = Seed.vehicle(jdbcTemplate, crashId, 1);
        struckByVehicleId = Seed.vehicle(jdbcTemplate, crashId, 2);
    }

    private static final String SEED_FULL = """
            INSERT INTO person (crash_id, person_number, occupant_vehicle_id, struck_by_vehicle_id,
                                date_of_birth, sex_code, road_user_type_code, seat_row_code,
                                seat_position_code, injury_severity_code, restraint_code, helmet_code,
                                ped_manoeuvre_code, alcohol_suspected_code, drug_use_code,
                                licence_status_code, licence_issue_date)
            VALUES (?, ?, ?, ?, '1990-06-05', 2, 2, 2, 3, 3, 10, 3, 1, 2, 3, 1, '2010-01-20')""";

    // The nullable ones: both vehicle links, date_of_birth, ped_manoeuvre_code,
    // licence_status_code, licence_issue_date
    private static final String SEED_MINIMAL = """
            INSERT INTO person (crash_id, person_number, sex_code, road_user_type_code,
                                seat_row_code, seat_position_code, injury_severity_code,
                                restraint_code, helmet_code, alcohol_suspected_code, drug_use_code)
            VALUES (?, ?, 2, 2, 2, 3, 3, 10, 3, 2, 3)""";

    @Test
    void findByCrashIdMapsEveryColumn() {
        jdbcTemplate.update(SEED_FULL, crashId, 1, occupantVehicleId, struckByVehicleId);

        Person person = personDao.findByCrashId(crashId).getFirst();

        assertEquals(crashId, person.getCrashId());
        assertEquals((short) 1, person.getPersonNumber());
        assertEquals(occupantVehicleId, person.getOccupantVehicleId());
        assertEquals(struckByVehicleId, person.getStruckByVehicleId());
        assertEquals(LocalDate.of(1990, 6, 5), person.getDateOfBirth());
        assertEquals(Sex.FEMALE, person.getSex());
        assertEquals(RoadUserType.PASSENGER, person.getRoadUserType());
        assertEquals(SeatRow.REAR, person.getSeatRow());
        assertEquals(SeatPosition.RIGHT, person.getSeatPosition());
        assertEquals(InjurySeverity.SLIGHT, person.getInjurySeverity());
        assertEquals(Restraint.NO_RESTRAINTS_USED, person.getRestraint());
        assertEquals(Helmet.NOT_APPLICABLE, person.getHelmet());
        assertEquals(PedManoeuvre.CROSSING, person.getPedManoeuvre());
        assertEquals(AlcoholSuspected.YES, person.getAlcoholSuspected());
        assertEquals(DrugUse.EVIDENCE, person.getDrugUse());
        assertEquals(LicenceStatus.ISSUED, person.getLicenceStatus());
        assertEquals(LocalDate.of(2010, 1, 20), person.getLicenceIssueDate());
    }

    // A passenger links to no vehicle and holds no licence, so these stay null
    @Test
    void findByCrashIdLeavesNullableColumnsNull() {
        jdbcTemplate.update(SEED_MINIMAL, crashId, 1);

        Person person = personDao.findByCrashId(crashId).getFirst();

        assertNull(person.getOccupantVehicleId());
        assertNull(person.getStruckByVehicleId());
        assertNull(person.getDateOfBirth());
        assertNull(person.getPedManoeuvre());
        assertNull(person.getLicenceStatus());
        assertNull(person.getLicenceIssueDate());
    }

    @Test
    void findByCrashIdOrdersByPersonNumber() {
        jdbcTemplate.update(SEED_MINIMAL, crashId, 3);
        jdbcTemplate.update(SEED_MINIMAL, crashId, 1);
        jdbcTemplate.update(SEED_MINIMAL, crashId, 2);

        List<Short> numbers = personDao.findByCrashId(crashId).stream()
                .map(Person::getPersonNumber)
                .toList();

        assertEquals(List.of((short) 1, (short) 2, (short) 3), numbers);
    }

    @Test
    void findByCrashIdIgnoresOtherCrashes() {
        long otherCrashId = Seed.crash(jdbcTemplate, "CD28-PER-OTHER", Seed.districtId(jdbcTemplate));
        jdbcTemplate.update(SEED_MINIMAL, crashId, 1);
        jdbcTemplate.update(SEED_MINIMAL, otherCrashId, 1);

        assertEquals(1, personDao.findByCrashId(crashId).size());
        assertEquals(crashId, personDao.findByCrashId(crashId).getFirst().getCrashId());
    }

    @Test
    void findByCrashIdReturnsEmptyWhenTheCrashHasNoPersons() {
        assertEquals(List.of(), personDao.findByCrashId(crashId));
    }

    @Test
    void insertWritesEveryColumnAndReturnsTheGeneratedId() {
        Person person = new Person(null, crashId, (short) 1, occupantVehicleId, struckByVehicleId,
                LocalDate.of(1990, 6, 5), Sex.FEMALE, RoadUserType.PASSENGER, SeatRow.REAR,
                SeatPosition.RIGHT, InjurySeverity.SLIGHT, Restraint.NO_RESTRAINTS_USED,
                Helmet.NOT_APPLICABLE, PedManoeuvre.CROSSING, AlcoholSuspected.YES, DrugUse.EVIDENCE,
                LicenceStatus.ISSUED, LocalDate.of(2010, 1, 20));

        Long id = personDao.insert(person);

        Map<String, Object> row = jdbcTemplate.queryForMap("SELECT * FROM person WHERE id = ?", id);
        assertEquals(crashId, ((Number) row.get("crash_id")).longValue());
        assertEquals((short) 1, ((Number) row.get("person_number")).shortValue());
        assertEquals(occupantVehicleId, ((Number) row.get("occupant_vehicle_id")).longValue());
        assertEquals(struckByVehicleId, ((Number) row.get("struck_by_vehicle_id")).longValue());
        assertEquals((short) 2, ((Number) row.get("sex_code")).shortValue());
        assertEquals((short) 2, ((Number) row.get("road_user_type_code")).shortValue());
        assertEquals((short) 2, ((Number) row.get("seat_row_code")).shortValue());
        assertEquals((short) 3, ((Number) row.get("seat_position_code")).shortValue());
        assertEquals((short) 3, ((Number) row.get("injury_severity_code")).shortValue());
        assertEquals((short) 10, ((Number) row.get("restraint_code")).shortValue());
        assertEquals((short) 3, ((Number) row.get("helmet_code")).shortValue());
        assertEquals((short) 1, ((Number) row.get("ped_manoeuvre_code")).shortValue());
        assertEquals((short) 2, ((Number) row.get("alcohol_suspected_code")).shortValue());
        assertEquals((short) 3, ((Number) row.get("drug_use_code")).shortValue());
        assertEquals((short) 1, ((Number) row.get("licence_status_code")).shortValue());
        assertEquals(LocalDate.of(1990, 6, 5), jdbcTemplate.queryForObject(
                "SELECT date_of_birth FROM person WHERE id = ?", LocalDate.class, id));
        assertEquals(LocalDate.of(2010, 1, 20), jdbcTemplate.queryForObject(
                "SELECT licence_issue_date FROM person WHERE id = ?", LocalDate.class, id));
    }

    @Test
    void insertWritesNullsForAPassengerWithNoVehicleOrLicence() {
        Long id = personDao.insert(new Person(null, crashId, (short) 1, null, null, null,
                Sex.FEMALE, RoadUserType.PASSENGER, SeatRow.REAR, SeatPosition.RIGHT,
                InjurySeverity.SLIGHT, Restraint.NO_RESTRAINTS_USED, Helmet.NOT_APPLICABLE,
                null, AlcoholSuspected.YES, DrugUse.EVIDENCE, null, null));

        Map<String, Object> row = jdbcTemplate.queryForMap("SELECT * FROM person WHERE id = ?", id);
        assertNull(row.get("occupant_vehicle_id"));
        assertNull(row.get("struck_by_vehicle_id"));
        assertNull(row.get("date_of_birth"));
        assertNull(row.get("ped_manoeuvre_code"));
        assertNull(row.get("licence_status_code"));
        assertNull(row.get("licence_issue_date"));
    }
}
