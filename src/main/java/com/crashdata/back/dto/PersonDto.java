package com.crashdata.back.dto;

import java.time.LocalDate;

public record PersonDto(
        Long id,
        Short personNumber,
        Long occupantVehicleId,
        Long struckByVehicleId,
        LocalDate dateOfBirth,
        Short sexCode,
        Short roadUserTypeCode,
        Short seatRowCode,
        Short seatPositionCode,
        Short injurySeverityCode,
        Short restraintCode,
        Short helmetCode,
        Short pedManoeuvreCode,
        Short alcoholSuspectedCode,
        Short drugUseCode,
        Short licenceStatusCode,
        LocalDate licenceIssueDate,
        AlcoholTestDto alcoholTest) {
}
