package com.crashdata.back.service;

import com.crashdata.back.dao.OverviewDao;
import com.crashdata.back.entity.DistrictCounts;
import com.crashdata.back.entity.Granularity;
import com.crashdata.back.entity.Overview;
import com.crashdata.back.entity.SeverityCounts;
import com.crashdata.back.entity.TrendPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OverviewServiceTest {

    @Mock
    private OverviewDao overviewDao;

    @InjectMocks
    private OverviewService service;

    @Test
    void overviewWindowsAreThirtyDaysEachAndAdjacent() {
        LocalDate to = LocalDate.of(2026, 9, 21);
        SeverityCounts counts = new SeverityCounts(10, 1, 2, 8, 0, 3);
        List<DistrictCounts> districts = List.of();
        when(overviewDao.kpis(LocalDate.of(2026, 7, 24), LocalDate.of(2026, 8, 23), to)).thenReturn(counts);
        when(overviewDao.topDistricts(LocalDate.of(2026, 7, 24), LocalDate.of(2026, 8, 23), to, 6))
                .thenReturn(districts);

        Overview overview = service.getOverview(to);

        assertEquals(LocalDate.of(2026, 8, 23), overview.from());
        assertEquals(to, overview.to());
        assertEquals(LocalDate.of(2026, 7, 24), overview.previousFrom());
        assertEquals(LocalDate.of(2026, 8, 22), overview.previousTo());
        assertEquals(counts, overview.counts());
        assertEquals(districts, overview.topDistricts());
    }

    @Test
    void dailyTrendHasThirtyPointsWithGapsFilledByZeros() {
        LocalDate to = LocalDate.of(2026, 9, 21);
        TrendPoint hit = new TrendPoint(LocalDate.of(2026, 9, 1), 4, 1, 1, 2);
        when(overviewDao.trend(Granularity.DAILY, LocalDate.of(2026, 8, 23), to)).thenReturn(List.of(hit));

        List<TrendPoint> series = service.getTrend(Granularity.DAILY, to);

        assertEquals(30, series.size());
        assertEquals(LocalDate.of(2026, 8, 23), series.get(0).periodStart());
        assertEquals(to, series.get(29).periodStart());
        assertEquals(hit, series.get(9));
        assertEquals(TrendPoint.empty(LocalDate.of(2026, 8, 24)), series.get(1));
    }

    @Test
    void weeklyTrendStartsOnAMondayTwelveWeeksBack() {
        // 2026-09-23 is a Wednesday; its week starts Monday 2026-09-21, 11 weeks earlier is 2026-07-06
        LocalDate to = LocalDate.of(2026, 9, 23);
        when(overviewDao.trend(eq(Granularity.WEEKLY), any(), any())).thenReturn(List.of());

        List<TrendPoint> series = service.getTrend(Granularity.WEEKLY, to);

        assertEquals(12, series.size());
        assertEquals(LocalDate.of(2026, 7, 6), series.get(0).periodStart());
        assertEquals(LocalDate.of(2026, 9, 21), series.get(11).periodStart());
    }

    @Test
    void monthlyTrendStartsOnTheFirstTwelveMonthsBack() {
        LocalDate to = LocalDate.of(2026, 9, 21);
        when(overviewDao.trend(Granularity.MONTHLY, LocalDate.of(2025, 10, 1), to)).thenReturn(List.of());

        List<TrendPoint> series = service.getTrend(Granularity.MONTHLY, to);

        assertEquals(12, series.size());
        assertEquals(LocalDate.of(2025, 10, 1), series.get(0).periodStart());
        assertEquals(LocalDate.of(2026, 9, 1), series.get(11).periodStart());
    }
}
