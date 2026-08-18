package com.crashdata.back.domain.location;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DistrictDao {

    private static final String BASE =
            "SELECT id, governorate_id, name_en, name_ar FROM district";

    private static final String FIND_ALL =
            BASE + " ORDER BY name_en";

    private static final String FIND_BY_GOVERNORATE =
            BASE + " WHERE governorate_id = ? ORDER BY name_en";

    private static final RowMapper<District> ROW_MAPPER = (rs, rowNum) -> new District(
            rs.getLong("id"),
            rs.getLong("governorate_id"),
            rs.getString("name_en"),
            rs.getString("name_ar"));

    private final JdbcTemplate jdbcTemplate;

    public DistrictDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<District> findByGovernorateId(Long governorateId) {
        return jdbcTemplate.query(FIND_BY_GOVERNORATE, ROW_MAPPER, governorateId);
    }

    public List<District> findAll() {
        return jdbcTemplate.query(FIND_ALL, ROW_MAPPER);
    }
}
