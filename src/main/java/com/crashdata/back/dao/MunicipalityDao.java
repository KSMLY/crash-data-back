package com.crashdata.back.dao;

import com.crashdata.back.entity.Municipality;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MunicipalityDao {

    private static final String BASE =
            "SELECT id, district_id, name_en, name_ar FROM municipality";

    private static final String FIND_ALL =
            BASE + " ORDER BY name_en";

    private static final String FIND_BY_DISTRICT =
            BASE + " WHERE district_id = ? ORDER BY name_en";

    private static final RowMapper<Municipality> ROW_MAPPER = (rs, rowNum) -> new Municipality(
            rs.getLong("id"),
            rs.getLong("district_id"),
            rs.getString("name_en"),
            rs.getString("name_ar"));

    private final JdbcTemplate jdbcTemplate;

    public MunicipalityDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Municipality> findByDistrictId(Long districtId) {
        return jdbcTemplate.query(FIND_BY_DISTRICT, ROW_MAPPER, districtId);
    }

    public List<Municipality> findAll() {
        return jdbcTemplate.query(FIND_ALL, ROW_MAPPER);
    }
}
