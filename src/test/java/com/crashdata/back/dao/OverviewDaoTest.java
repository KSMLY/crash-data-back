package com.crashdata.back.dao;

import com.crashdata.back.entity.DistrictCounts;
import com.crashdata.back.entity.Granularity;
import com.crashdata.back.entity.SeverityCounts;
import com.crashdata.back.entity.TrendPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OverviewDao.class)
class OverviewDaoTest {

    // Windows in 1990 so rows already in the database cannot land inside them
    private static final LocalDate PREVIOUS_FROM = LocalDate.of(1990, 1, 1);
    private static final LocalDate FROM = LocalDate.of(1990, 1, 31);
    private static final LocalDate TO = LocalDate.of(1990, 3, 1);

    private static final int FATAL = 1;
    private static final int SERIOUS = 2;
    private static final int SLIGHT = 3;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    OverviewDao overviewDao;

    private long districtA;
    private long districtB;
    private int nextRef;

    @BeforeEach
    void readSeededDistricts() {
        List<Long> ids = jdbcTemplate.queryForList("SELECT TOP 2 id FROM district ORDER BY id", Long.class);
        districtA = ids.get(0);
        districtB = ids.get(1);
    }

    // Raw SQL rather than CrashDao, so the aggregates are checked against rows the
    // DAO under test had no hand in writing
    private void seed(LocalDate crashDate, int severity, long districtId) {
        jdbcTemplate.update("""
                INSERT INTO crash (police_ref, ref_year, crash_date, district_id,
                                   crash_type_code, impact_type_code, weather_code, light_code,
                                   severity_code, roadway_type_code, speed_limit_kmh,
                                   obstacle_present_code, surface_condition_code,
                                   junction_type_code, curve_code, grade_code)
                VALUES (?, 1990, ?, ?, 5, 10, 4, 2, ?, 6, 80, 9, 5, 6, 2, 2)""",
                "CD44-" + nextRef++, crashDate, districtId, severity);
    }

    @Test
    void kpisSplitTheSpanAtFromAndIgnoreRowsOutsideIt() {
        seed(PREVIOUS_FROM.minusDays(1), FATAL, districtA);   // before the previous window
        seed(PREVIOUS_FROM, FATAL, districtA);                // previous
        seed(FROM.minusDays(1), SERIOUS, districtA);          // previous, last day
        seed(FROM, FATAL, districtA);                         // current, first day
        seed(TO, SERIOUS, districtA);                         // current, last day
        seed(TO, SLIGHT, districtA);                          // current
        seed(TO.plusDays(1), FATAL, districtA);               // after the window

        SeverityCounts counts = overviewDao.kpis(PREVIOUS_FROM, FROM, TO);

        assertEquals(new SeverityCounts(3, 1, 1, 2, 1, 1), counts);
    }

    @Test
    void kpisAreZeroWhenNothingMatches() {
        assertEquals(new SeverityCounts(0, 0, 0, 0, 0, 0), overviewDao.kpis(PREVIOUS_FROM, FROM, TO));
    }

    @Test
    void topDistrictsOrderByCurrentTotalAndHonourTheLimit() {
        seed(FROM, FATAL, districtA);
        seed(FROM, SERIOUS, districtB);
        seed(TO, SLIGHT, districtB);
        seed(PREVIOUS_FROM, SLIGHT, districtB);   // counts only towards previousTotal
        seed(PREVIOUS_FROM, SLIGHT, districtA);
        seed(PREVIOUS_FROM, SLIGHT, districtA);

        List<DistrictCounts> top = overviewDao.topDistricts(PREVIOUS_FROM, FROM, TO, 6);

        assertEquals(2, top.size());
        DistrictCounts first = top.get(0);
        assertEquals(districtB, first.district().getId());
        assertEquals(2, first.total());
        assertEquals(0, first.fatal());
        assertEquals(1, first.serious());
        assertEquals(1, first.previousTotal());
        DistrictCounts second = top.get(1);
        assertEquals(districtA, second.district().getId());
        assertEquals(1, second.total());
        assertEquals(1, second.fatal());
        assertEquals(2, second.previousTotal());

        assertEquals(1, overviewDao.topDistricts(PREVIOUS_FROM, FROM, TO, 1).size());
    }

    @Test
    void topDistrictsMapTheDistrictColumns() {
        seed(FROM, SLIGHT, districtA);

        DistrictCounts counts = overviewDao.topDistricts(PREVIOUS_FROM, FROM, TO, 6).get(0);

        assertEquals(jdbcTemplate.queryForObject(
                        "SELECT name_en FROM district WHERE id = ?", String.class, districtA),
                counts.district().getNameEn());
        assertEquals(jdbcTemplate.queryForObject(
                        "SELECT governorate_id FROM district WHERE id = ?", Long.class, districtA),
                counts.district().getGovernorateId());
    }

    @Test
    void dailyTrendReturnsOnlyDaysWithCrashes() {
        seed(FROM, FATAL, districtA);
        seed(FROM, SLIGHT, districtA);
        seed(FROM.plusDays(2), SERIOUS, districtA);
        seed(FROM.minusDays(1), SLIGHT, districtA);   // outside

        List<TrendPoint> points = overviewDao.trend(Granularity.DAILY, FROM, TO);

        assertEquals(List.of(
                new TrendPoint(FROM, 2, 1, 0, 1),
                new TrendPoint(FROM.plusDays(2), 1, 0, 1, 0)), points);
    }

    @Test
    void weeklyTrendBucketsStartOnMonday() {
        // 1990-02-05 is a Monday
        LocalDate monday = LocalDate.of(1990, 2, 5);
        seed(monday, SLIGHT, districtA);
        seed(monday.plusDays(6), SLIGHT, districtA);   // Sunday, same week
        seed(monday.plusDays(7), SLIGHT, districtA);   // next Monday

        List<TrendPoint> points = overviewDao.trend(Granularity.WEEKLY, FROM, TO);

        assertEquals(List.of(
                new TrendPoint(monday, 2, 0, 0, 2),
                new TrendPoint(monday.plusDays(7), 1, 0, 0, 1)), points);
    }

    @Test
    void monthlyTrendBucketsStartOnTheFirst() {
        seed(LocalDate.of(1990, 2, 10), SLIGHT, districtA);
        seed(LocalDate.of(1990, 2, 28), FATAL, districtA);
        seed(LocalDate.of(1990, 3, 1), SLIGHT, districtA);

        List<TrendPoint> points = overviewDao.trend(Granularity.MONTHLY, FROM, TO);

        assertEquals(List.of(
                new TrendPoint(LocalDate.of(1990, 2, 1), 2, 1, 0, 1),
                new TrendPoint(LocalDate.of(1990, 3, 1), 1, 0, 0, 1)), points);
    }
}
