package com.crashdata.back.dao;

import com.crashdata.back.entity.Municipality;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class MunicipalityDao {

    private static final String FIND_ALL = Sql.load("sql/municipality/find-all.sql");
    private static final String FIND_BY_DISTRICT = Sql.load("sql/municipality/find-by-district.sql");

    private static final RowMapper<Municipality> ROW_MAPPER = (rs, rowNum) -> new Municipality(
            rs.getLong("id"),
            rs.getLong("district_id"),
            rs.getString("name_en"),
            rs.getString("name_ar"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Municipality> findByDistrictId(Long districtId) {
        return jdbcTemplate.query(FIND_BY_DISTRICT,
                new MapSqlParameterSource("district_id", districtId), ROW_MAPPER);
    }

    public List<Municipality> findAll() {
        return jdbcTemplate.query(FIND_ALL, Map.of(), ROW_MAPPER);
    }
}
