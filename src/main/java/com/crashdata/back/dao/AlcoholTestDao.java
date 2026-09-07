package com.crashdata.back.dao;

import com.crashdata.back.code.ResultStatus;
import com.crashdata.back.code.TestStatus;
import com.crashdata.back.code.TestType;
import com.crashdata.back.entity.AlcoholTest;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.crashdata.back.code.CodedEnum.codeOf;

@Repository
@AllArgsConstructor
public class AlcoholTestDao {

    private static final String FIND_BY_CRASH = Sql.load("sql/alcohol-test/find-by-crash.sql");
    private static final String INSERT = Sql.load("sql/alcohol-test/insert.sql");

    private static final RowMapper<AlcoholTest> ROW_MAPPER = (rs, rowNum) -> new AlcoholTest(
            rs.getLong("person_id"),
            Codes.of(rs, "test_status_code", TestStatus.class),
            Codes.of(rs, "test_type_code", TestType.class),
            Codes.of(rs, "result_status_code", ResultStatus.class),
            rs.getBigDecimal("result_value"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Map<Long, AlcoholTest> findByCrashId(Long crashId) {
        return jdbcTemplate.query(FIND_BY_CRASH, new MapSqlParameterSource("crash_id", crashId), ROW_MAPPER)
                .stream()
                .collect(Collectors.toMap(AlcoholTest::getPersonId, Function.identity()));
    }

    public void insert(AlcoholTest test) {
        jdbcTemplate.update(INSERT, parameters(test));
    }

    private static SqlParameterSource parameters(AlcoholTest test) {
        return new MapSqlParameterSource()
                .addValue("person_id", test.getPersonId())
                .addValue("test_status_code", codeOf(test.getTestStatus()))
                .addValue("test_type_code", codeOf(test.getTestType()))
                .addValue("result_status_code", codeOf(test.getResultStatus()))
                .addValue("result_value", test.getResultValue());
    }
}
