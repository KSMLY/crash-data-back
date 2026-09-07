package com.crashdata.back.dao;

import com.crashdata.back.code.ResultStatus;
import com.crashdata.back.code.TestStatus;
import com.crashdata.back.code.TestType;
import com.crashdata.back.entity.AlcoholTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AlcoholTestDao.class)
class AlcoholTestDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AlcoholTestDao alcoholTestDao;

    private long crashId;

    @BeforeEach
    void seedCrash() {
        crashId = Seed.crash(jdbcTemplate, "CD28-ALC", Seed.districtId(jdbcTemplate));
    }

    private static final String SEED = """
            INSERT INTO alcohol_test (person_id, test_status_code, test_type_code,
                                      result_status_code, result_value)
            VALUES (?, 3, 2, 1, 0.85)""";

    // result_value is only filled when the result is available
    private static final String SEED_PENDING = """
            INSERT INTO alcohol_test (person_id, test_status_code, test_type_code,
                                      result_status_code, result_value)
            VALUES (?, 3, 2, 2, NULL)""";

    @Test
    void findByCrashIdMapsEveryColumn() {
        long personId = Seed.person(jdbcTemplate, crashId, 1);
        jdbcTemplate.update(SEED, personId);

        AlcoholTest test = alcoholTestDao.findByCrashId(crashId).get(personId);

        assertEquals(personId, test.getPersonId());
        assertEquals(TestStatus.GIVEN, test.getTestStatus());
        assertEquals(TestType.BREATH, test.getTestType());
        assertEquals(ResultStatus.AVAILABLE, test.getResultStatus());
        assertEquals(0, new BigDecimal("0.85").compareTo(test.getResultValue()));
    }

    @Test
    void findByCrashIdLeavesAPendingResultValueNull() {
        long personId = Seed.person(jdbcTemplate, crashId, 1);
        jdbcTemplate.update(SEED_PENDING, personId);

        assertNull(alcoholTestDao.findByCrashId(crashId).get(personId).getResultValue());
    }

    // The query reaches alcohol_test through person, so the key has to be the person
    // it belongs to and not the crash
    @Test
    void findByCrashIdKeysEachTestByItsOwnPerson() {
        long tested = Seed.person(jdbcTemplate, crashId, 1);
        long alsoTested = Seed.person(jdbcTemplate, crashId, 2);
        Seed.person(jdbcTemplate, crashId, 3);
        jdbcTemplate.update(SEED, tested);
        jdbcTemplate.update(SEED_PENDING, alsoTested);

        Map<Long, AlcoholTest> tests = alcoholTestDao.findByCrashId(crashId);

        assertEquals(Set.of(tested, alsoTested), tests.keySet());
        assertEquals(ResultStatus.AVAILABLE, tests.get(tested).getResultStatus());
        assertEquals(ResultStatus.PENDING, tests.get(alsoTested).getResultStatus());
    }

    @Test
    void findByCrashIdIgnoresOtherCrashes() {
        long otherCrashId = Seed.crash(jdbcTemplate, "CD28-ALC-OTHER", Seed.districtId(jdbcTemplate));
        long personId = Seed.person(jdbcTemplate, crashId, 1);
        long otherPersonId = Seed.person(jdbcTemplate, otherCrashId, 1);
        jdbcTemplate.update(SEED, personId);
        jdbcTemplate.update(SEED, otherPersonId);

        assertEquals(Set.of(personId), alcoholTestDao.findByCrashId(crashId).keySet());
    }

    @Test
    void findByCrashIdReturnsEmptyWhenNobodyWasTested() {
        Seed.person(jdbcTemplate, crashId, 1);

        assertEquals(Map.of(), alcoholTestDao.findByCrashId(crashId));
    }

    @Test
    void insertWritesEveryColumn() {
        long personId = Seed.person(jdbcTemplate, crashId, 1);

        alcoholTestDao.insert(new AlcoholTest(personId, TestStatus.GIVEN, TestType.BREATH,
                ResultStatus.AVAILABLE, new BigDecimal("0.85")));

        Map<String, Object> row = jdbcTemplate.queryForMap(
                "SELECT * FROM alcohol_test WHERE person_id = ?", personId);
        assertEquals((short) 3, ((Number) row.get("test_status_code")).shortValue());
        assertEquals((short) 2, ((Number) row.get("test_type_code")).shortValue());
        assertEquals((short) 1, ((Number) row.get("result_status_code")).shortValue());
        assertEquals(0, new BigDecimal("0.85").compareTo((BigDecimal) row.get("result_value")));
    }

    @Test
    void insertWritesNullForAPendingResult() {
        long personId = Seed.person(jdbcTemplate, crashId, 1);

        alcoholTestDao.insert(new AlcoholTest(personId, TestStatus.GIVEN, TestType.BREATH,
                ResultStatus.PENDING, null));

        assertNull(jdbcTemplate.queryForObject(
                "SELECT result_value FROM alcohol_test WHERE person_id = ?", BigDecimal.class, personId));
    }
}
