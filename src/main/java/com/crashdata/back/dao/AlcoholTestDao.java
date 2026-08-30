package com.crashdata.back.dao;

import com.crashdata.back.code.ResultStatus;
import com.crashdata.back.code.TestStatus;
import com.crashdata.back.code.TestType;
import com.crashdata.back.entity.AlcoholTest;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class AlcoholTestDao {

    private static final String FIND_BY_CRASH = """
            SELECT a.person_id, a.test_status_code, a.test_type_code,
                   a.result_status_code, a.result_value
            FROM alcohol_test a
            JOIN person p ON p.id = a.person_id
            WHERE p.crash_id = ?""";

    private static final RowMapper<AlcoholTest> ROW_MAPPER = (rs, rowNum) -> new AlcoholTest(
            rs.getLong("person_id"),
            Codes.of(rs, "test_status_code", TestStatus.class),
            Codes.of(rs, "test_type_code", TestType.class),
            Codes.of(rs, "result_status_code", ResultStatus.class),
            rs.getBigDecimal("result_value"));

    private final JdbcTemplate jdbcTemplate;

    public Map<Long, AlcoholTest> findByCrashId(Long crashId) {
        return jdbcTemplate.query(FIND_BY_CRASH, ROW_MAPPER, crashId).stream()
                .collect(Collectors.toMap(AlcoholTest::getPersonId, Function.identity()));
    }
}
