package com.crashdata.back.dao;

import com.crashdata.back.entity.District;
import com.crashdata.back.entity.DistrictCounts;
import com.crashdata.back.entity.Granularity;
import com.crashdata.back.entity.SeverityCounts;
import com.crashdata.back.entity.TrendPoint;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Read-only aggregates for the overview page. Windows are inclusive on both ends. */
@Repository
@AllArgsConstructor
public class OverviewDao {

    private static final String KPIS = Sql.load("sql/crash/overview-kpis.sql");
    private static final String TOP_DISTRICTS = Sql.load("sql/crash/overview-districts.sql");
    private static final String TREND = Sql.load("sql/crash/overview-trend.sql");

    // SUM over no rows is NULL; getLong turns that into 0, which is the count we want
    private static final RowMapper<SeverityCounts> KPI_MAPPER = (rs, rowNum) -> new SeverityCounts(
            rs.getLong("total"),
            rs.getLong("fatal"),
            rs.getLong("serious"),
            rs.getLong("previous_total"),
            rs.getLong("previous_fatal"),
            rs.getLong("previous_serious"));

    private static final RowMapper<DistrictCounts> DISTRICT_MAPPER = (rs, rowNum) -> new DistrictCounts(
            new District(
                    rs.getLong("district_id"),
                    rs.getLong("governorate_id"),
                    rs.getString("name_en"),
                    rs.getString("name_ar")),
            rs.getLong("total"),
            rs.getLong("fatal"),
            rs.getLong("serious"),
            rs.getLong("previous_total"));

    private static final RowMapper<TrendPoint> TREND_MAPPER = (rs, rowNum) -> new TrendPoint(
            rs.getObject("period_start", LocalDate.class),
            rs.getLong("total"),
            rs.getLong("fatal"),
            rs.getLong("serious"),
            rs.getLong("slight"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SeverityCounts kpis(LocalDate previousFrom, LocalDate from, LocalDate to) {
        return Objects.requireNonNull(
                jdbcTemplate.queryForObject(KPIS, windows(previousFrom, from, to), KPI_MAPPER));
    }

    public List<DistrictCounts> topDistricts(LocalDate previousFrom, LocalDate from, LocalDate to, int limit) {
        return jdbcTemplate.query(TOP_DISTRICTS,
                windows(previousFrom, from, to).addValue("limit", limit), DISTRICT_MAPPER);
    }

    /** Buckets with no crashes are absent from the result; the caller fills them in. */
    public List<TrendPoint> trend(Granularity granularity, LocalDate from, LocalDate to) {
        // DATETRUNC's unit cannot be a bind parameter, so it comes from the enum, never from the request
        String sql = String.format(TREND, granularity.getSqlUnit(), granularity.getSqlUnit());
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from_date", from)
                .addValue("to_date", to);
        return jdbcTemplate.query(sql, params, TREND_MAPPER);
    }

    private static MapSqlParameterSource windows(LocalDate previousFrom, LocalDate from, LocalDate to) {
        return new MapSqlParameterSource()
                .addValue("previous_from", previousFrom)
                .addValue("from_date", from)
                .addValue("to_date", to);
    }
}
