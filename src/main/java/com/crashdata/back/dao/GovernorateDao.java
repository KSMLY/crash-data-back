package com.crashdata.back.dao;

import com.crashdata.back.entity.Governorate;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class GovernorateDao {

    private static final String FIND_ALL =
            "SELECT id, name_en, name_ar FROM governorate ORDER BY name_en";

    private static final RowMapper<Governorate> ROW_MAPPER = (rs, rowNum) -> new Governorate(
            rs.getLong("id"),
            rs.getString("name_en"),
            rs.getString("name_ar"));

    private final JdbcTemplate jdbcTemplate;

    public List<Governorate> findAll() {
        return jdbcTemplate.query(FIND_ALL, ROW_MAPPER);
    }
}