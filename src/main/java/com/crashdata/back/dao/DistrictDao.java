package com.crashdata.back.dao;

import com.crashdata.back.entity.District;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class DistrictDao {

    private static final String FIND_ALL = Sql.load("sql/district/find-all.sql");
    private static final String FIND_BY_GOVERNORATE = Sql.load("sql/district/find-by-governorate.sql");

    private static final RowMapper<District> ROW_MAPPER = (rs, rowNum) -> new District(
            rs.getLong("id"),
            rs.getLong("governorate_id"),
            rs.getString("name_en"),
            rs.getString("name_ar"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<District> findByGovernorateId(Long governorateId) {
        return jdbcTemplate.query(FIND_BY_GOVERNORATE,
                new MapSqlParameterSource("governorate_id", governorateId), ROW_MAPPER);
    }

    public List<District> findAll() {
        return jdbcTemplate.query(FIND_ALL, Map.of(), ROW_MAPPER);
    }
}
