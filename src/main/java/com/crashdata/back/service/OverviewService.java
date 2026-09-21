package com.crashdata.back.service;

import com.crashdata.back.dao.OverviewDao;
import com.crashdata.back.entity.Granularity;
import com.crashdata.back.entity.Overview;
import com.crashdata.back.entity.TrendPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OverviewService {

    private static final int WINDOW_DAYS = 30;
    private static final int TOP_DISTRICTS = 6;
    private static final int TREND_WEEKS = 12;
    private static final int TREND_MONTHS = 12;

    private final OverviewDao overviewDao;

    /** The 30 days ending at {@code to}, compared with the 30 days before them. */
    public Overview getOverview(LocalDate to) {
        LocalDate from = to.minusDays(WINDOW_DAYS - 1);
        LocalDate previousTo = from.minusDays(1);
        LocalDate previousFrom = previousTo.minusDays(WINDOW_DAYS - 1);
        return new Overview(
                from, to, previousFrom, previousTo,
                overviewDao.kpis(previousFrom, from, to),
                overviewDao.topDistricts(previousFrom, from, to, TOP_DISTRICTS));
    }

    /**
     * One point per bucket up to {@code to}. The query only returns buckets that have
     * crashes, so the gaps are filled with zeros here to keep the series a fixed length.
     */
    public List<TrendPoint> getTrend(Granularity granularity, LocalDate to) {
        LocalDate start = windowStart(granularity, to);
        Map<LocalDate, TrendPoint> byPeriod = overviewDao.trend(granularity, start, to).stream()
                .collect(Collectors.toMap(TrendPoint::periodStart, Function.identity()));

        List<TrendPoint> series = new ArrayList<>();
        for (LocalDate period = start; !period.isAfter(to); period = next(granularity, period)) {
            series.add(byPeriod.getOrDefault(period, TrendPoint.empty(period)));
        }
        return series;
    }

    // The first bucket is aligned to its natural start (Monday, 1st of the month) so it
    // matches what DATETRUNC returns for the crashes inside it
    private static LocalDate windowStart(Granularity granularity, LocalDate to) {
        return switch (granularity) {
            case DAILY -> to.minusDays(WINDOW_DAYS - 1);
            case WEEKLY -> to.minusWeeks(TREND_WEEKS - 1).with(DayOfWeek.MONDAY);
            case MONTHLY -> to.minusMonths(TREND_MONTHS - 1).withDayOfMonth(1);
        };
    }

    private static LocalDate next(Granularity granularity, LocalDate period) {
        return switch (granularity) {
            case DAILY -> period.plusDays(1);
            case WEEKLY -> period.plusWeeks(1);
            case MONTHLY -> period.plusMonths(1);
        };
    }
}
