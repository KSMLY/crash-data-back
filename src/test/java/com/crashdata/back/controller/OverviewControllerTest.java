package com.crashdata.back.controller;

import com.crashdata.back.entity.District;
import com.crashdata.back.entity.DistrictCounts;
import com.crashdata.back.entity.Granularity;
import com.crashdata.back.entity.Overview;
import com.crashdata.back.entity.SeverityCounts;
import com.crashdata.back.entity.TrendPoint;
import com.crashdata.back.service.OverviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OverviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class OverviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OverviewService overviewService;

    @Test
    void overviewUnpacksTheCountsIntoCurrentAndPrevious() throws Exception {
        LocalDate to = LocalDate.of(2026, 9, 21);
        when(overviewService.getOverview(to)).thenReturn(new Overview(
                LocalDate.of(2026, 8, 23), to, LocalDate.of(2026, 7, 24), LocalDate.of(2026, 8, 22),
                new SeverityCounts(1284, 62, 318, 1196, 68, 283),
                List.of(new DistrictCounts(new District(11L, 9L, "Beirut", "بيروت"), 214, 9, 54, 202))));

        mockMvc.perform(get("/overview").param("to", "2026-09-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("2026-08-23"))
                .andExpect(jsonPath("$.previousTo").value("2026-08-22"))
                .andExpect(jsonPath("$.total.current").value(1284))
                .andExpect(jsonPath("$.total.previous").value(1196))
                .andExpect(jsonPath("$.fatal.current").value(62))
                .andExpect(jsonPath("$.fatal.previous").value(68))
                .andExpect(jsonPath("$.serious.current").value(318))
                .andExpect(jsonPath("$.serious.previous").value(283))
                .andExpect(jsonPath("$.topDistricts[0].district.nameEn").value("Beirut"))
                .andExpect(jsonPath("$.topDistricts[0].total").value(214))
                .andExpect(jsonPath("$.topDistricts[0].previousTotal").value(202));
    }

    @Test
    void overviewDefaultsToToday() throws Exception {
        when(overviewService.getOverview(any())).thenReturn(new Overview(
                null, null, null, null, new SeverityCounts(0, 0, 0, 0, 0, 0), List.of()));

        mockMvc.perform(get("/overview")).andExpect(status().isOk());

        verify(overviewService).getOverview(LocalDate.now());
    }

    @Test
    void overviewRejectsABadDate() throws Exception {
        mockMvc.perform(get("/overview").param("to", "yesterday"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("to is not valid."));
    }

    @Test
    void trendMapsEveryField() throws Exception {
        LocalDate to = LocalDate.of(2026, 9, 21);
        when(overviewService.getTrend(Granularity.WEEKLY, to))
                .thenReturn(List.of(new TrendPoint(LocalDate.of(2026, 9, 14), 43, 2, 11, 30)));

        mockMvc.perform(get("/overview/trend").param("granularity", "WEEKLY").param("to", "2026-09-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].periodStart").value("2026-09-14"))
                .andExpect(jsonPath("$[0].total").value(43))
                .andExpect(jsonPath("$[0].fatal").value(2))
                .andExpect(jsonPath("$[0].serious").value(11))
                .andExpect(jsonPath("$[0].slight").value(30));
    }

    @Test
    void trendDefaultsToDailyUpToToday() throws Exception {
        when(overviewService.getTrend(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/overview/trend")).andExpect(status().isOk());

        verify(overviewService).getTrend(Granularity.DAILY, LocalDate.now());
    }

    @Test
    void trendListsTheAllowedGranularities() throws Exception {
        mockMvc.perform(get("/overview/trend").param("granularity", "hourly"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("granularity must be one of: DAILY, WEEKLY, MONTHLY"));
    }
}
