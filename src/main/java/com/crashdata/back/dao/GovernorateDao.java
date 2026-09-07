package com.crashdata.back.dao;

import com.crashdata.back.entity.Governorate;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class GovernorateDao {

    private static final String FIND_ALL = Sql.load("sql/governorate/find-all.sql");

    private static final RowMapper<Governorate> ROW_MAPPER = (rs, rowNum) -> new Governorate(
            rs.getLong("id"),
            rs.getString("name_en"),
            rs.getString("name_ar"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Governorate> findAll() {
        return jdbcTemplate.query(FIND_ALL, Map.of(), ROW_MAPPER);
    }
}
