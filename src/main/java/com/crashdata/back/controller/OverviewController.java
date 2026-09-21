package com.crashdata.back.controller;

import com.crashdata.back.dto.CountsDto;
import com.crashdata.back.dto.DistrictCountsDto;
import com.crashdata.back.dto.DistrictDto;
import com.crashdata.back.dto.OverviewDto;
import com.crashdata.back.dto.OverviewRequest;
import com.crashdata.back.dto.TrendPointDto;
import com.crashdata.back.dto.TrendRequest;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.DistrictCounts;
import com.crashdata.back.entity.Overview;
import com.crashdata.back.entity.SeverityCounts;
import com.crashdata.back.entity.TrendPoint;
import com.crashdata.back.service.OverviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class OverviewController {

    private final OverviewService overviewService;

    @GetMapping("/overview")
    public OverviewDto getOverview(@Valid OverviewRequest request) {
        return toDto(overviewService.getOverview(request.toOrToday()));
    }

    @GetMapping("/overview/trend")
    public List<TrendPointDto> getTrend(@Valid TrendRequest request) {
        return overviewService.getTrend(request.granularityOrDaily(), request.toOrToday()).stream()
                .map(OverviewController::toDto)
                .toList();
    }

    private static OverviewDto toDto(Overview overview) {
        SeverityCounts counts = overview.counts();
        return new OverviewDto(
                overview.from(),
                overview.to(),
                overview.previousFrom(),
                overview.previousTo(),
                new CountsDto(counts.total(), counts.previousTotal()),
                new CountsDto(counts.fatal(), counts.previousFatal()),
                new CountsDto(counts.serious(), counts.previousSerious()),
                overview.topDistricts().stream().map(OverviewController::toDto).toList());
    }

    private static DistrictCountsDto toDto(DistrictCounts counts) {
        return new DistrictCountsDto(
                toDto(counts.district()),
                counts.total(),
                counts.fatal(),
                counts.serious(),
                counts.previousTotal());
    }

    private static DistrictDto toDto(District district) {
        return new DistrictDto(district.getId(), district.getGovernorateId(),
                district.getNameEn(), district.getNameAr());
    }

    private static TrendPointDto toDto(TrendPoint point) {
        return new TrendPointDto(point.periodStart(), point.total(), point.fatal(), point.serious(), point.slight());
    }
}
